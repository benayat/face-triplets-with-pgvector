package org.benaya.ai.parquet_to_postgres_triplets.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benaya.ai.parquet_to_postgres_triplets.model.LabelToEmbedding;
import org.benaya.ai.parquet_to_postgres_triplets.model.LabelToEmbeddingId;
import org.benaya.ai.parquet_to_postgres_triplets.model.Triplet;
import org.benaya.ai.parquet_to_postgres_triplets.model.TripletPartSearchResult;
import org.benaya.ai.parquet_to_postgres_triplets.repository.LabelToEmbeddingNativeRepository;
import org.benaya.ai.parquet_to_postgres_triplets.repository.LabelToEmbeddingRepository;
import org.benaya.ai.parquet_to_postgres_triplets.repository.TripletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripletService {

    private final LabelToEmbeddingRepository labelToEmbeddingRepository;
    private final TripletRepository tripletRepository;
    private final LabelToEmbeddingNativeRepository labelToEmbeddingNativeRepository;

    public void createTripletsForBatchAnchors(List<LabelToEmbedding> anchors) {
        List<Triplet> triplets = anchors.stream().map(this::createTriplet).toList();
        int batchSize = 1000;
        for (int i = 0; i < triplets.size(); i += batchSize) {
            int end = Math.min(i + batchSize, triplets.size());
            List<Triplet> batch = triplets.subList(i, end);
            tripletRepository.saveAll(batch);
        }
    }

    public void processAllLabelToEmbeddings() {
        int pageSize = 1000;
        int processedCount = 0;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<LabelToEmbedding> page;

        log.info("Starting to process all LabelToEmbeddings in batches of {}", pageSize);
        do {
            page = labelToEmbeddingRepository.findAll(pageable);
            List<LabelToEmbedding> labelToEmbeddings = page.getContent();
            List<Triplet> triplets = labelToEmbeddings.stream().map(this::createTripletWithJpa).toList();
            tripletRepository.saveAll(triplets);
            processedCount += labelToEmbeddings.size();
            log.info("Processed {} entities so far", processedCount);
            // Process the batch of labelToEmbeddings
            pageable = pageable.next();
        } while (page.hasNext());
        log.info("Finished processing all LabelToEmbeddings. Total processed: {}", processedCount);
    }


    private Triplet createTriplet(LabelToEmbedding anchor){
        TripletPartSearchResult positive = labelToEmbeddingRepository.getPositiveByAnchorAndCosineDistance(Arrays.toString(anchor.getFaceEmbedding()), anchor.getId().getLabel(), anchor.getId().getPath());
        LabelToEmbeddingId positiveId = new LabelToEmbeddingId(positive.getLabel(), positive.getPath());
        Object[] resultObj = (Object[]) labelToEmbeddingNativeRepository.getNegative(anchor.getId().getLabel(), Arrays.toString(anchor.getFaceEmbedding()), 850);
        LabelToEmbeddingId negativeId = new LabelToEmbeddingId((Long)resultObj[0], (String) resultObj[1]);
        return new Triplet(anchor.getId(), positiveId, negativeId);
    }
    private Triplet createTripletWithJpa(LabelToEmbedding anchor){
        TripletPartSearchResult positive = labelToEmbeddingRepository.getPositiveByAnchorAndCosineDistance(Arrays.toString(anchor.getFaceEmbedding()), anchor.getId().getLabel(), anchor.getId().getPath());
        LabelToEmbeddingId positiveId = new LabelToEmbeddingId(positive.getLabel(), positive.getPath());
        TripletPartSearchResult negative = labelToEmbeddingRepository.getNegativeByAnchorAndCosineDistance(Arrays.toString(anchor.getFaceEmbedding()), anchor.getId().getLabel());
        LabelToEmbeddingId negativeId = new LabelToEmbeddingId(negative.getLabel(), negative.getPath());
        return new Triplet(anchor.getId(), positiveId, negativeId);
    }
}
