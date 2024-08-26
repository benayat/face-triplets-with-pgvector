//package org.benaya.ai.parquet_to_postgres_triplets.service;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class IndexService {
//    @PersistenceContext
//    private final EntityManager entityManager;
//
//    @Transactional
//    public void createHnswIndex() {
//        String sql = "CREATE INDEX ON LableToEmbedding USING hnsw (face_embedding vector_cosine_ops);";
//        entityManager.createNativeQuery(sql).executeUpdate();
//    }
//}
