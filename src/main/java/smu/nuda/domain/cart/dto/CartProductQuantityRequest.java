package smu.nuda.domain.cart.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartProductQuantityRequest {
    @NotNull private Integer delta;
}
