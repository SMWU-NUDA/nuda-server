package smu.nuda.domain.like.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.review.entity.Review;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "review_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_review_like_unique",
                        columnNames = {"review_id", "member_id"}
                )
        }
)
public class ReviewLike extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "review_like_seq"
    )
    @SequenceGenerator(
            name = "review_like_seq",
            sequenceName = "review_like_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "review_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_like_review")
    )
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_like_member")
    )
    private Member member;

    public ReviewLike(Review review, Member member) {
        this.review = review;
        this.member = member;
    }
}