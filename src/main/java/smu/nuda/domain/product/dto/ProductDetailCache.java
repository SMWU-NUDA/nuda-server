package smu.nuda.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductDetailCache {
    private Long productId;

    private List<String> imageUrls;
    private String brandName;
    private String name;
    private Double averageRating;
    private Integer reviewCount;
    private Integer price;

    private String content;
}
