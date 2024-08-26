import pandas as pd
import torch
from datasets import load_dataset, Image as ImageDs
from facenet_pytorch.models.inception_resnet_v1 import InceptionResnetV1
from torchvision.transforms import transforms
import logging
from io import BytesIO
from PIL import Image

logging.basicConfig(level=logging.INFO)
model = InceptionResnetV1(pretrained='vggface2').eval().to('cuda')
dataset = (load_dataset("chronopt-research/cropped-vggface2-224", split='train')
           .cast_column('image', ImageDs(decode=False)))

batch_size = 1000
group_size = 100  # Number of batches to save in each group


def preprocess_image(image):
    """
    Preprocess an image for embedding extraction. This method can be static because
    it does not depend on the instance or class.
    """
    preprocess = transforms.Compose([
        transforms.Resize((160, 160)),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.6062257, 0.45110273, 0.3793817], std=[0.20877807, 0.1811061, 0.16814634])
    ])
    return preprocess(image)


# Function to convert binary data to a tensor
def process_binary_image(image_bytes):
    # Decode the binary data into an image using PIL
    image = Image.open(BytesIO(image_bytes))

    # Apply preprocessing (resize, convert to tensor, normalize)
    image_tensor = preprocess_image(image)
    return image_tensor


def get_embeddings_for_batch(image_list):
    """
    Get embeddings for a batch of images with CUDA OOM error handling.
    """
    # Preprocess the batch of images and move to GPU
    img_tensors_stacked = torch.stack(image_list).to('cuda')
    initial_chunk_size = 1000  # Initial chunk size
    chunk_size = initial_chunk_size  # Start with the initial chunk size
    num_chunks = (len(img_tensors_stacked) + chunk_size - 1) // chunk_size  # Calculate the number of chunks
    all_embeddings = []

    # Extract embeddings for the batch and keep them on the GPU
    with torch.no_grad():
        i = 0
        while i < num_chunks:
            try:
                start_idx = i * chunk_size
                end_idx = min((i + 1) * chunk_size, len(img_tensors_stacked))
                chunk = img_tensors_stacked[start_idx:end_idx]
                chunk_embeddings = model(chunk).to('cuda')  # Process chunk
                # normalized_embeddings = normalize_embeddings(chunk_embeddings)
                # all_embeddings.append(normalized_embeddings)
                all_embeddings.append(chunk_embeddings)
                i += 1  # Proceed to the next chunk

                # Release GPU memory after processing each chunk
                del chunk, chunk_embeddings
                torch.cuda.empty_cache()

            except RuntimeError as e:
                if 'CUDA out of memory' in str(e):
                    # Halve the chunk size and retry the current chunk
                    torch.cuda.empty_cache()
                    chunk_size = max(1, chunk_size // 2)
                    num_chunks = (len(img_tensors_stacked) + chunk_size - 1) // chunk_size  # Recalculate the number of chunks
                    logging.info(f"CUDA OOM: Reducing chunk size to {chunk_size}")
                else:
                    raise e  # Raise any other errors

    return torch.cat(all_embeddings, dim=0)  # Concatenate all chunk embeddings


def process_and_save_to_parquet():
    """Processes images in batches, extracts embeddings, and saves them to a Parquet file."""
    batch_data_list = []
    # group_count = 1
    for i in range(0, len(dataset), batch_size):
        batch = dataset[i:i + batch_size]
        images = batch['image']  # Original 224x224 images
        images_tensors = [process_binary_image(image['bytes']) for image in images]  # Binary image data
        labels = batch['label']
        images_paths = [image['path'] for image in images]

        # Get embeddings for the current batch
        embeddings = get_embeddings_for_batch(images_tensors)

        # Process each image, embedding, and label
        for j, path in enumerate(images_paths):
            batch_data_list.append({
                'label': labels[j],
                'path': path,
                'embedding': embeddings[j].cpu().numpy(),
            })
        if i % 1000000 == 0 or i == len(dataset) - 1:
            df = pd.DataFrame(batch_data_list)
            batch_data_list = []
            df.to_parquet(f'dataset_with_embeddings_part{int(i / 1000000)}.parquet')
        if i % 100000 == 0:
            logging.info(f"processed {i} images")


process_and_save_to_parquet()
