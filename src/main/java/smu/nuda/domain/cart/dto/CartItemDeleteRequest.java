package smu.nuda.domain.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CartItemDeleteRequest {
    private List<Long> cartItemIds;
}
