package smu.nuda.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyOrderResponse {
    private Long orderId;
    private String orderDate;
    private Long orderNum;
    private int totalAmount;
    private List<OrderBrandGroup> brands;
}
