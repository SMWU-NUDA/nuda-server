package smu.nuda.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.like.entity.ReviewLike;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.review.entity.Review;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByMemberAndReview(Member member, Review review);

    @Query("""
    select rl.review.id
    from ReviewLike rl
    where rl.member.id = :memberId
      and rl.review.id in :reviewIds
""")
    Set<Long> findLikedReviewIds(
            @Param("memberId") Long memberId,
            @Param("reviewIds") List<Long> reviewIds
    );

    @Modifying
    @Query("DELETE FROM ReviewLike rl WHERE rl.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);
}
