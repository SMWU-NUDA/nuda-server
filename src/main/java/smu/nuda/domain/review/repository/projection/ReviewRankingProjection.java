package smu.nuda.domain.review.repository.projection;

import java.time.LocalDateTime;

public interface ReviewRankingProjection {
    Long getReviewId();
    Long getProductId();

    Long getMemberId();
    String getMemberUsername();
    String getMemberNickname();
    String getMemberProfileImg();
    String getMemberEmail();

    Double getRating();
    Long getLikeCount();
    String getContent();
    LocalDateTime getCreatedAt();
}
