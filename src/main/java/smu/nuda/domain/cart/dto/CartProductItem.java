package smu.nuda.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartProductItem {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private int quantity;
    private int price;
    private int totalPrice; // quantity * price
}
