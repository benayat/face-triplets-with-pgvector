package org.benaya.ai.parquet_to_postgres_triplets.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
public class Triplet {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "label", column = @Column(name = "anchor_label")),
            @AttributeOverride(name = "path", column = @Column(name = "anchor_path"))
    })
    private LabelToEmbeddingId anchorId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "label", column = @Column(name = "positive_label")),
            @AttributeOverride(name = "path", column = @Column(name = "positive_path"))
    })
    private LabelToEmbeddingId positiveId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "label", column = @Column(name = "negative_label")),
            @AttributeOverride(name = "path", column = @Column(name = "negative_path"))
    })
    private LabelToEmbeddingId negativeId;
}