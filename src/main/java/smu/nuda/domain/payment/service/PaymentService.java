package smu.nuda.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.cart.service.CartService;
import smu.nuda.domain.log.entity.enums.CommerceType;
import smu.nuda.domain.log.event.CommerceEvent;
import smu.nuda.domain.member.dto.DeliveryResponse;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.order.entity.Order;
import smu.nuda.domain.order.entity.OrderItem;
import smu.nuda.domain.order.mapper.OrderMapper;
import smu.nuda.domain.order.repository.OrderRepository;
import smu.nuda.domain.payment.dto.PaymentCompleteRequest;
import smu.nuda.domain.payment.dto.PaymentCompleteResponse;
import smu.nuda.domain.payment.dto.PaymentRequestResponse;
import smu.nuda.domain.payment.entity.Payment;
import smu.nuda.domain.payment.entity.enums.PaymentStatus;
import smu.nuda.domain.payment.error.PaymentErrorCode;
import smu.nuda.domain.payment.repository.PaymentRepository;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.error.DomainException;

import java.time.Clock;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentRequestResponse requestPayment(Long memberId, Long orderId) {

        // 요청한 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(PaymentErrorCode.ORDER_NOT_FOUND));

        // 주문 상태 검증 및 중복 결제 방지
        if (!order.getMemberId().equals(memberId)) throw new DomainException(PaymentErrorCode.NOT_ORDER_OWNER);
        if (!order.isPending()) throw new DomainException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        if (paymentRepository.existsByOrder(order)) throw new DomainException(PaymentErrorCode.ALREADY_REQUESTED);

        int amount = order.getTotalAmount();
        String paymentKey = generatePaymentKey();

        // Payment 생성
        Payment payment = Payment.request(order, paymentKey, amount, clock);
        paymentRepository.save(payment);

        return new PaymentRequestResponse(
                payment.getId(),
                order.getId(),
                order.getOrderNum(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentKey(),
                buildRedirectUrl(payment)
        );
    }

    private String generatePaymentKey() {
        return "pay_" + UUID.randomUUID();
    }

    private String buildRedirectUrl(Payment payment) {
        return "https://pg.test/pay/" + payment.getPaymentKey();
    }

    @Transactional
    public PaymentCompleteResponse completePayment(Member member, PaymentCompleteRequest request) {

        // 요청한 paymentKey에 해당하는 Payment, Order 조회
        Payment payment = paymentRepository.findByPaymentKey(request.getPaymentKey())
                .orElseThrow(() -> new DomainException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        Order order = payment.getOrder();

        // 결제 가능 여부 검증
        if (payment.getStatus() == PaymentStatus.SUCCESS) throw new DomainException(PaymentErrorCode.ALREADY_PAID);
        if (!order.getId().equals(request.getOrderId())) throw new DomainException(PaymentErrorCode.ORDER_MISMATCH);
        if (payment.getAmount() != request.getAmount()) throw new DomainException(PaymentErrorCode.INVALID_AMOUNT);
        if (!Boolean.TRUE.equals(request.getSuccess())) {
            payment.fail();
            order.failPayment();
            return PaymentCompleteResponse.fail(order.getOrderNum());
        }

        payment.approve();
        order.completePayment();

        // log 이벤트 발행
        Set<Long> productIds = order.getOrderItems().stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = productMap.get(orderItem.getProductId());
            if (product == null) continue;

            eventPublisher.publishEvent(
                    new CommerceEvent(
                            order.getMemberId(),
                            orderItem.getProductId(),
                            product.getExternalProductId(),
                            CommerceType.PURCHASE,
                            orderItem.getQuantity()
                    )
            );
        }

        // 결제 완료한 장바구니 상품 삭제
        cartService.removeOrderedItems(order);

        int totalAmount = order.getOrderItems().stream()
                .mapToInt(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        return new PaymentCompleteResponse(
                order.getOrderNum(),
                totalAmount,
                DeliveryResponse.from(member),
                orderMapper.toBrandGroups(order)
        );
    }

    @Transactional
    public PaymentCompleteResponse completeTestPayment(Member member, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new DomainException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        Order order = payment.getOrder();

        if (payment.getStatus() == PaymentStatus.SUCCESS) throw new DomainException(PaymentErrorCode.ALREADY_PAID);
        if (payment.getStatus() != PaymentStatus.REQUESTED) throw new DomainException(PaymentErrorCode.INVALID_PAYMENT_STATUS);

        // 서버 기준으로 강제 승인
        payment.approve();
        order.completePayment();

        // log 이벤트 발행
        Set<Long> productIds = order.getOrderItems().stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = productMap.get(orderItem.getProductId());
            if (product == null) continue;

            eventPublisher.publishEvent(
                    new CommerceEvent(
                            order.getMemberId(),
                            orderItem.getProductId(),
                            product.getExternalProductId(),
                            CommerceType.PURCHASE,
                            orderItem.getQuantity()
                    )
            );
        }

        // 결제 완료한 장바구니 상품 삭제
        cartService.removeOrderedItems(order);

        int totalAmount = order.getOrderItems().stream()
                .mapToInt(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        return new PaymentCompleteResponse(
                order.getOrderNum(),
                totalAmount,
                DeliveryResponse.from(member),
                orderMapper.toBrandGroups(order)
        );
    }

}
