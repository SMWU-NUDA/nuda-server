package smu.nuda.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductItem {
    private Long productId;
    private String thumbnailImg;
    private Long brandId;
    private String brandName;
    private String productName;
    private double averageRating;
    private int reviewCount;
    private int likeCount;
    private int costPrice;
}
