## face-triplets-with-pgvector
- Hard-mining face triplets with pgvector, using triplets made by facenet model on the entire vggface2 dataset.
- The input is arrow files containing the entities - each has label, path, and embedding(dim=512).
- The program will create a hard mined triplet for each anchor entity, using hnsw index with pgvector - the triplet will contain the anchor, positive, and negative sets of label+file-path.
- At this point, the embedding has done its job, and the next level will be using the resulting triplets for training a face-recognition dnn model.

## requirements
- conda 4.7.1 with python3.12 for running the scripts.
- jdk-22.
- docker+docker compose for running the pgvector database.

## running instructions 
- First, run the LoadArrowFileRunner to load the entities to the pgvector database. 
- Then, run the CreateAndSaveTripletsRunner to create the triplets and save them to the pgvector database.

## todo:
- upload the resulting db to huggingface.