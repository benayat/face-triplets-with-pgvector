package org.benaya.ai.parquet_to_postgres_triplets.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class LabelToEmbeddingId implements Serializable {
    private Long label;
    private String path;
}