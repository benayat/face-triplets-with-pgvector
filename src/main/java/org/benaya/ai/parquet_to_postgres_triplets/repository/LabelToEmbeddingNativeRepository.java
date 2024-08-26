package org.benaya.ai.parquet_to_postgres_triplets.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LabelToEmbeddingNativeRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    //    create a method to run the following in pgvector; """
//        BEGIN;
//        SET LOCAL hnsw.ef_search = 850;
//        WITH top_k_neighbors AS MATERIALIZED (
//            SELECT label, path, face_embedding
//            FROM lable_to_embedding
//            ORDER BY face_embedding <=> :vector ASC
//            LIMIT 850
//        )
//        SELECT label, path
//        FROM top_k_neighbors
//        WHERE label != :label
//        ORDER BY face_embedding <=> :vector ASC
//        LIMIT 1;
//        COMMIT;
//    """; and return the result
    @Transactional
    public Object getNegative(long label, String vector, int efSearch) {
        String query = """
                    WITH top_k_neighbors AS MATERIALIZED (
                        SELECT label, path, face_embedding
                        FROM lable_to_embedding
                        ORDER BY face_embedding <=> CAST(:vector as vector) ASC
                        LIMIT :efSearch
                    ) SELECT label, path
                    FROM top_k_neighbors
                    WHERE label != :label
                    ORDER BY face_embedding <=> CAST(:vector as vector) ASC
                    LIMIT 1;
                """;
        entityManager.createNativeQuery("SET LOCAL hnsw.ef_search = 850").executeUpdate();
        return entityManager.createNativeQuery(query)
                .setParameter("label", label)
                .setParameter("vector", vector)
                .setParameter("efSearch", efSearch)
                .getSingleResult();
    }
}
