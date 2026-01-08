package smu.nuda.domain.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductDetailResponse {
    private Long productId;

    private List<String> imageUrls;
    private String brandName;
    private boolean likedByMe;
    private String name;
    private double averageRating;
    private long reviewCount;
    private int price;

    private String content;

    public static ProductDetailResponse of(ProductDetailCache cache, boolean likedByMe) {
        return ProductDetailResponse.builder()
                .productId(cache.getProductId())
                .imageUrls(cache.getImageUrls())
                .brandName(cache.getBrandName())
                .name(cache.getName())
                .averageRating(cache.getAverageRating())
                .reviewCount(cache.getReviewCount())
                .price(cache.getPrice())
                .content(cache.getContent())
                .likedByMe(likedByMe)
                .build();
    }

}
