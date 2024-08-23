package org.benaya.ai.parquet_to_postgres_triplets.repository;

import org.benaya.ai.parquet_to_postgres_triplets.model.LabelToEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelToEmbeddingRepository extends JpaRepository<LabelToEmbedding, Integer> {
}
