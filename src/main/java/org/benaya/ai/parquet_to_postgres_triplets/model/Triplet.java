package org.benaya.ai.parquet_to_postgres_triplets.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
public class Triplet {
    @EmbeddedId
    private LabelToEmbeddingId anchorId;
    @Embedded
    private LabelToEmbeddingId positiveId;
    @Embedded
    private LabelToEmbeddingId negativeId;
}
