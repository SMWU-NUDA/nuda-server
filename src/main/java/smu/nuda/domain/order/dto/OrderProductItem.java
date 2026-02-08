package smu.nuda.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderProductItem {
    private Long productId;
    private String productName;
    private int quantity;
    private int price;
    private int totalPrice; // quantity * price

}
