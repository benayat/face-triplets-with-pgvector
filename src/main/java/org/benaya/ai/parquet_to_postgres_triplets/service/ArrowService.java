package org.benaya.ai.parquet_to_postgres_triplets.service;

import lombok.RequiredArgsConstructor;
import org.apache.arrow.vector.Float4Vector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.benaya.ai.parquet_to_postgres_triplets.model.LabelToEmbedding;
import org.benaya.ai.parquet_to_postgres_triplets.model.LabelToEmbeddingId;
import org.benaya.ai.parquet_to_postgres_triplets.repository.LabelToEmbeddingRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArrowService {

    private final ArrowFileReader arrowFileReader;
    private final LabelToEmbeddingRepository labelToEmbeddingRepository;

    public void readArrowObject() throws IOException {
        VectorSchemaRoot root = arrowFileReader.getVectorSchemaRoot();

        IntVector labelVector = (IntVector) root.getVector("label");
        VarCharVector pathVector = (VarCharVector) root.getVector("path");
        Float4Vector embeddingVector = (Float4Vector) root.getVector("embedding");
        List<LabelToEmbedding> labelToEmbeddings = new ArrayList<>();
        while (arrowFileReader.loadNextBatch()) {
            int rowCount = root.getRowCount();

            for (int i = 0; i < rowCount; i++) {
                int label = labelVector.get(i);
                String path = pathVector.getObject(i).toString();

                float[] embedding = new float[embeddingVector.getValueCount()];
                for (int j = 0; j < embeddingVector.getValueCount(); j++) {
                    embedding[j] = embeddingVector.get(i);
                }

                // Map to DataEntity
                labelToEmbeddings.add(LabelToEmbedding.builder()
                        .id(new LabelToEmbeddingId(label, path))
                        .faceEmbedding(embedding)
                        .build());

            }
            labelToEmbeddingRepository.saveAll(labelToEmbeddings);
            labelToEmbeddings.clear();
        }
    }
}
