package smu.nuda.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartBrandGroup {
    private Long brandId;
    private String brandName;
    private List<CartProductItem> products;
}
