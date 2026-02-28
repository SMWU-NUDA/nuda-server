package smu.nuda.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductItem {
    private Long productId;
    private String thumbnailImg;
    private Long brandId;
    private String brandName;
    private String productName;
    List<String> ingredientLabels;
    private double averageRating;
    private int reviewCount;
    private int likeCount;
    private int costPrice;

    public ProductItem(
            Long productId,
            String thumbnailImg,
            Long brandId,
            String brandName,
            String productName,
            Double averageRating,
            Integer reviewCount,
            Integer likeCount,
            Integer costPrice
    ) {
        this.productId = productId;
        this.thumbnailImg = thumbnailImg;
        this.brandId = brandId;
        this.brandName = brandName;
        this.productName = productName;
        this.averageRating = averageRating != null ? averageRating : 0.0;
        this.reviewCount = reviewCount != null ? reviewCount : 0;
        this.likeCount = likeCount != null ? likeCount : 0;
        this.costPrice = costPrice != null ? costPrice : 0;
    }
}
