package smu.nuda.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.member.dto.DeliveryResponse;
import smu.nuda.domain.order.dto.OrderBrandGroup;

import java.util.List;

@Getter
@AllArgsConstructor
public class PaymentCompleteResponse {
    private Long orderNum;
    private DeliveryResponse deliveryResponse;
    private List<OrderBrandGroup> brands;
}
