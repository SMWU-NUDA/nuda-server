package smu.nuda.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailCache {
    private Long productId;

    private List<String> mainImageUrls;
    private Long brandId;
    private String brandName;
    private String name;
    private Double averageRating;
    private Integer reviewCount;
    private Integer price;

    private List<String> detailImageUrls;
    private String content;
}
