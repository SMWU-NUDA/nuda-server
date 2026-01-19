package smu.nuda.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.global.util.DateFormatUtil;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyReviewResponse {
    private Long productId;
    private String productThumbnail;
    private String productName;
    private String brandName;

    private Long reviewId;
    private double rating;
    private String content;
    private String createdAt;

    public MyReviewResponse(Long productId, String productThumbnail, String productName, String brandName, Long reviewId, double rating, String content, LocalDateTime createdAt) {
        this.productId = productId;
        this.productThumbnail = productThumbnail;
        this.productName = productName;
        this.brandName = brandName;
        this.reviewId = reviewId;
        this.rating = rating;
        this.content = content;
        this.createdAt = DateFormatUtil.formatDate(createdAt);
    }
}
