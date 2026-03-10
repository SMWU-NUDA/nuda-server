package smu.nuda.domain.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductDetailResponse {
    private Long productId;

    private List<String> mainImageUrls;
    private Long brandId;
    private String brandName;
    private boolean brandLikedByMe;
    private String name;
    private double averageRating;
    private long reviewCount;
    private boolean productLikedByMe;
    private int price;

    private List<String> detailImageUrls;
    private String content;

    public static ProductDetailResponse of(ProductDetailCache cache, boolean productLikedByMe, boolean brandLikedByMe) {
        return ProductDetailResponse.builder()
                .productId(cache.getProductId())
                .mainImageUrls(cache.getMainImageUrls())
                .brandName(cache.getBrandName())
                .name(cache.getName())
                .averageRating(cache.getAverageRating())
                .reviewCount(cache.getReviewCount())
                .price(cache.getPrice())
                .content(cache.getContent())
                .productLikedByMe(productLikedByMe)
                .brandLikedByMe(brandLikedByMe)
                .detailImageUrls(cache.getDetailImageUrls())
                .brandId(cache.getBrandId())
                .build();
    }

}
