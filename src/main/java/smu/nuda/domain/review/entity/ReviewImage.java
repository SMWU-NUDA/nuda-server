package smu.nuda.domain.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "review_image",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_review_image_sequence",
                        columnNames = {"review_id", "sequence"}
                )
        }
)
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "review_image_seq"
    )
    @SequenceGenerator(
            name = "review_image_seq",
            sequenceName = "review_image_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "review_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_image_review")
    )
    private Review review;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private int sequence;

    public ReviewImage(Review review, String imageUrl, int sequence) {
        this.review = review;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }
}
