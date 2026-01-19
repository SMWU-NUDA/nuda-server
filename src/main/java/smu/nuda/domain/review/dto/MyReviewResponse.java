package smu.nuda.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
