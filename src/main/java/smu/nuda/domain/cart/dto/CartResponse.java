package smu.nuda.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponse {
    private List<CartBrandGroup> brands;
    private int totalQuantity;
    private int totalPrice;
}
