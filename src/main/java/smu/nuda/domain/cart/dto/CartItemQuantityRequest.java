package smu.nuda.domain.cart.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemQuantityRequest {
    @NotNull private Integer delta;
}
