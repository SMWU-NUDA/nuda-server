package smu.nuda.domain.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductDetailCache {
    private Long productId;

    private List<String> imageUrls;
    private String brandName;
    private String name;
    private double averageRating;
    private long reviewCount;
    private int price;

    private String content;
}
