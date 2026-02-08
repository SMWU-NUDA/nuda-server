package smu.nuda.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.nuda.domain.member.dto.DeliveryResponse;
import smu.nuda.domain.order.dto.OrderBrandGroup;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PaymentCompleteResponse {
    private Long orderNum;
    private DeliveryResponse deliveryResponse;
    private List<OrderBrandGroup> brands;

    public static PaymentCompleteResponse fail(Long orderNum) {
        return PaymentCompleteResponse.builder()
                .orderNum(orderNum)
                .deliveryResponse(null)
                .brands(null)
                .build();
    }
}
