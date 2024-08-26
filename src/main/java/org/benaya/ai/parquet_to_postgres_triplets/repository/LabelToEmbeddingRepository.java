package org.benaya.ai.parquet_to_postgres_triplets.repository;

import jakarta.transaction.Transactional;
import org.benaya.ai.parquet_to_postgres_triplets.model.LabelToEmbedding;
import org.benaya.ai.parquet_to_postgres_triplets.model.LabelToEmbeddingId;
import org.benaya.ai.parquet_to_postgres_triplets.model.TripletPartSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

public interface LabelToEmbeddingRepository extends JpaRepository<LabelToEmbedding, Integer> {
    @Transactional
    @Query(value = "SELECT l.label, l.path FROM lable_to_embedding l WHERE l.label != :label ORDER BY l.face_embedding <=> CAST(:vector AS vector) ASC LIMIT 1", nativeQuery = true)
    TripletPartSearchResult getNegativeByAnchorAndCosineDistance(@Param("vector") String vector, @Param("label") Long label);

    @Query(value = "SELECT l.label, l.path FROM (SELECT * FROM lable_to_embedding l WHERE l.label != :label ORDER BY l.face_embedding <=> CAST(:vector AS vector) ASC LIMIT :limit) l LIMIT 1", nativeQuery = true)
    TripletPartSearchResult getNegativeByAnchorAndCosineDistanceWithLimit(@Param("vector") String vector, @Param("label") Long label, @Param("limit") int limit);

    @Query(value = "SELECT l.label, l.path FROM lable_to_embedding l WHERE l.label != :label ORDER BY cosine_distance(l.face_embedding, CAST(:vector AS vector)) ASC LIMIT 1", nativeQuery = true)
    TripletPartSearchResult getNegativeByAnchorAndCosineDistanceFunc(@Param("vector") String vector, @Param("label") Long label);

    @Query(value = "SELECT l.label, l.path FROM lable_to_embedding l WHERE l.label = :label AND l.path != :path ORDER BY l.face_embedding <=> CAST(:vector AS vector) DESC LIMIT 1", nativeQuery = true)
    TripletPartSearchResult getPositiveByAnchorAndCosineDistance(@Param("vector") String vector, @Param("label") Long label, @Param("path") String path);

    @Query(value = "SELECT l.label, l.path FROM lable_to_embedding l WHERE l.label = :label AND l.path != :path ORDER BY cosine_distance(l.face_embedding, CAST(:vector AS vector)) DESC LIMIT 1", nativeQuery = true)
    TripletPartSearchResult getPositiveByAnchorAndCosineDistanceFunc(@Param("vector") String vector, @Param("label") Long label, @Param("path") String path);

    @NonNull
    Page<LabelToEmbedding> findAll(@NonNull Pageable pageable);
}
