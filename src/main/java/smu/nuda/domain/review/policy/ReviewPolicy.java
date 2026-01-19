package smu.nuda.domain.review.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.error.ReviewErrorCode;
import smu.nuda.global.error.DomainException;

@Component
@RequiredArgsConstructor
public class ReviewPolicy {
    public void validateOwner(Review review, Member member) {
        if (!review.isWrittenBy(member)) {
            throw new DomainException(ReviewErrorCode.NOT_REVIEW_OWNER);
        }
    }
}
