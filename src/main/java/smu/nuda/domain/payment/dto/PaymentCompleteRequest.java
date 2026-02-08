package smu.nuda.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompleteRequest {
    @NotNull private String paymentKey;
    @NotNull private Long orderId;
    @NotNull private Integer amount;
    @NotNull private Boolean success;

}
