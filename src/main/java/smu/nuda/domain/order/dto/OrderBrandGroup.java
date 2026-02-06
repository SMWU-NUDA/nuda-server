package smu.nuda.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderBrandGroup {
    private Long brandId;
    private String brandName;
    private List<OrderProductItem> products;
}
