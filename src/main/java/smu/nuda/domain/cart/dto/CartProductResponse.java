package smu.nuda.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CartProductResponse {
    private Long productId;
    private int quantity;

}
