package smu.nuda.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderCreateResponse {
    private Long orderId;
    private Long orderNum;
    private String status;
    private int totalAmount;
    private List<OrderBrandGroup> brands;

}
