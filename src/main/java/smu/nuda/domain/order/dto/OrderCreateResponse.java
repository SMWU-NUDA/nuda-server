package smu.nuda.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.order.entity.enums.OrderStatus;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderCreateResponse {
    private Long orderId;
    private Long orderNum;
    private OrderStatus status;
    private int totalAmount;
    private List<OrderBrandGroup> brands;

}
