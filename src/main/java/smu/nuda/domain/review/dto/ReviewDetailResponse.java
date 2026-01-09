package smu.nuda.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import smu.nuda.domain.member.dto.MeResponse;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.global.util.DateFormatUtil;

import java.util.List;

@Getter
@Builder
public class ReviewDetailResponse {
    private Long reviewId;
    private Long productId;

    private MeResponse me;

    private Double rating;
    private Long likeCount;
    private boolean likedByMe;
    private String pros;
    private String cons;
    private List<String> imageUrls;

    private String createdAt;

    public static ReviewDetailResponse of(Review review, List<String> imageUrls, boolean likedByMe) {
        return ReviewDetailResponse.builder()
                .reviewId(review.getId())
                .productId(review.getProduct().getId())
                .me(MeResponse.from(review.getMember()))
                .rating(review.getRating())
                .likeCount((long) review.getLikeCount())
                .likedByMe(likedByMe)
                .pros(review.getPros())
                .cons(review.getCons())
                .imageUrls(imageUrls)
                .createdAt(DateFormatUtil.formatDate(review.getCreatedAt()))
                .build();
    }

}
