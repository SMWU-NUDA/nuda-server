package smu.nuda.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.order.entity.Order;
import smu.nuda.domain.order.repository.OrderRepository;
import smu.nuda.domain.payment.dto.PaymentReqResponse;
import smu.nuda.domain.payment.entity.Payment;
import smu.nuda.domain.payment.error.PaymentErrorCode;
import smu.nuda.domain.payment.repository.PaymentRepository;
import smu.nuda.global.error.DomainException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentReqResponse requestPayment(Long orderId) {

        // 요청한 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(PaymentErrorCode.ORDER_NOT_FOUND));

        // 주문 상태 검증 및 중복 결제 방지
        if (!order.isPending()) throw new DomainException(PaymentErrorCode.INVALID_ORDER_STATUS);
        if (paymentRepository.existsByOrder(order)) throw new DomainException(PaymentErrorCode.ALREADY_REQUESTED);

        int amount = order.getTotalAmount();
        String paymentKey = generatePaymentKey(order);

        // Payment 생성
        Payment payment = Payment.request(order, paymentKey, amount);
        paymentRepository.save(payment);

        return new PaymentReqResponse(
                payment.getId(),
                order.getId(),
                order.getOrderNum(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentKey(),
                buildRedirectUrl(payment)
        );
    }

    private String generatePaymentKey(Order order) {
        return "pay_" + UUID.randomUUID();
    }

    private String buildRedirectUrl(Payment payment) {
        return "https://pg.test/pay/" + payment.getPaymentKey();
    }
}
