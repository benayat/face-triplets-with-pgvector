package org.benaya.ai.parquet_to_postgres_triplets.repository;

import org.benaya.ai.parquet_to_postgres_triplets.model.Triplet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripletRepository extends JpaRepository<Triplet, Integer> {
}
