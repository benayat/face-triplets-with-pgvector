package org.benaya.ai.parquet_to_postgres_triplets.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.complex.ListVector;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.apache.arrow.vector.ipc.SeekableReadChannel;
import org.benaya.ai.parquet_to_postgres_triplets.model.LabelToEmbedding;
import org.benaya.ai.parquet_to_postgres_triplets.model.LabelToEmbeddingId;
import org.benaya.ai.parquet_to_postgres_triplets.repository.LabelToEmbeddingRepository;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArrowService {

    private final List<Path> arrowFilePaths;
    private final RootAllocator rootAllocator;
    private final LabelToEmbeddingRepository labelToEmbeddingRepository;
//    private final IndexService indexService;

    public void readArrowFilesWriteToDb() throws IOException {
        for (Path filePath : arrowFilePaths) {
            try (FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
                 SeekableReadChannel seekableReadChannel = new SeekableReadChannel(fileInputStream.getChannel());
                 ArrowFileReader arrowFileReader = new ArrowFileReader(seekableReadChannel, rootAllocator)) {
                VectorSchemaRoot root = arrowFileReader.getVectorSchemaRoot();
                BigIntVector labelVector = (BigIntVector) root.getVector("label");
                VarCharVector pathVector = (VarCharVector) root.getVector("path");
                ListVector embeddingVector = (ListVector) root.getVector("embedding");
                List<LabelToEmbedding> labelToEmbeddings = new ArrayList<>();
                log.info("Reading Arrow file: {}", filePath);
                int count = 0;
                while (arrowFileReader.loadNextBatch()) {
                    int rowCount = root.getRowCount();
                    log.info("in batch number {} with {} rows", count++, rowCount);
                    for (int i = 0; i < rowCount; i++) {
                        long label = labelVector.get(i);
                        String path = pathVector.getObject(i).toString();
                        List<Float> vector = (List<Float>) embeddingVector.getObject(i);
                        float[] floatVector = new float[vector.size()];
                        for (int j = 0; j < vector.size(); j++) {
                            floatVector[j] = vector.get(j);
                        }
                        // Map to DataEntity
                        labelToEmbeddings.add(LabelToEmbedding.builder()
                                .id(new LabelToEmbeddingId(label, path))
                                .faceEmbedding(floatVector)
                                .build());
                    }
                    labelToEmbeddingRepository.saveAll(labelToEmbeddings);
                    labelToEmbeddings.clear();
                }
            } catch (Exception e) {
                log.error("Error processing file {}: {}", filePath, e.getMessage());
            }
        }
//        indexService.createHnswIndex();
    }
}
