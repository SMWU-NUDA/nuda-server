package smu.nuda.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.like.entity.ReviewLike;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.review.entity.Review;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByMemberAndReview(Member member, Review review);
}
