package smu.nuda.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderHistoryResponse {
    private List<OrderHistoryItem> orders;
}
