package smu.nuda.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedProductResponse {
    private Long likeId;
    private Long productId;

    private String thumbnailImg;
    private String brandName;
    private String productName;

    private double averageRating;
    private int reviewCount;
}
