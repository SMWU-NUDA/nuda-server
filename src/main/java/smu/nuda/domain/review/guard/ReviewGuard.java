package smu.nuda.domain.review.guard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.error.ReviewErrorCode;
import smu.nuda.domain.review.policy.ReviewPolicy;
import smu.nuda.domain.review.repository.ReviewRepository;
import smu.nuda.global.error.DomainException;

@Component
@RequiredArgsConstructor
public class ReviewGuard {
    private final ReviewRepository reviewRepository;
    private final ReviewPolicy reviewPolicy;

    public Review validateDeletable(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new DomainException(ReviewErrorCode.INVALID_REVIEW));

        reviewPolicy.validateOwner(review, member);
        return review;
    }
}
