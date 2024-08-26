package org.benaya.ai.parquet_to_postgres_triplets.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
@Qualifier(value = "transactionManager")
public class EfSearchTransactionManager extends JpaTransactionManager {

    private final EntityManager entityManager;

    @Override
    protected void prepareSynchronization(@NonNull DefaultTransactionStatus status, @NonNull TransactionDefinition definition) {
        super.prepareSynchronization(status, definition);
        if (status.isNewTransaction()) {
            final String query = "SET SESSION hnsw.ef_search = 850";
            entityManager.createNativeQuery(query).executeUpdate();
        }
    }

}
