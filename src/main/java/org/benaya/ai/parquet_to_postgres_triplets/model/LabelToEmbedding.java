package org.benaya.ai.parquet_to_postgres_triplets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@Table(name = "LableToEmbedding", indexes = {
        @Index(name = "idx_face_embedding", columnList = "face_embedding")
})
public class LabelToEmbedding {
    @EmbeddedId
    private LabelToEmbeddingId id;
    @Column(name = "face_embedding")
    @JdbcTypeCode(value = SqlTypes.VECTOR)
    @Array(length = 512)
    @JsonIgnore
    private float[] faceEmbedding;
}
