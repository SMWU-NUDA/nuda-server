package smu.nuda.domain.review.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.repository.projection.ReviewRankingProjection;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
    select r.id as reviewId,
           p.id as productId,
           m.id as memberId,
           m.username as memberUsername,
           m.nickname as memberNickname,
           m.profileImg as memberProfileImg,
           m.email as memberEmail,
           r.rating as rating,
           r.likeCount as likeCount,
           r.content as content,
           r.createdAt as createdAt
    from Review r
    join r.product p
    join r.member m
    where r.id in :reviewIds
""")
    List<ReviewRankingProjection> findRankingReviews(@Param("reviewIds") List<Long> reviewIds);

    @Query("""
        select r.id
        from Review r
        where r.product.id = :productId
        order by r.likeCount desc, r.createdAt desc
    """)
    List<Long> findFallbackTopIds(@Param("productId") Long productId, Pageable pageable);
}
