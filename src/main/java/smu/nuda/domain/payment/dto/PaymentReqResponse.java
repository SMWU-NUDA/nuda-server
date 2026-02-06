package smu.nuda.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.payment.entity.enums.PaymentMethod;
import smu.nuda.domain.payment.entity.enums.Status;

@Getter
@AllArgsConstructor
public class PaymentReqResponse {
    private Long paymentId;
    private Long orderId;
    private Long orderNum;
    private int amount; // 결제 금액
    private Status status; // REQUESTED

    // PG 연동 대비
    private String paymentKey;
    private String redirectUrl;
//    private PaymentMethod method;
}
