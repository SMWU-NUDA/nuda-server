package smu.nuda.domain.payment.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.cart.entity.Cart;
import smu.nuda.domain.cart.entity.CartItem;
import smu.nuda.domain.cart.repository.CartItemRepository;
import smu.nuda.domain.cart.repository.CartRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.order.dto.OrderCreateRequest;
import smu.nuda.domain.order.dto.OrderCreateResponse;
import smu.nuda.domain.order.dto.OrderItemRequest;
import smu.nuda.domain.order.entity.Order;
import smu.nuda.domain.order.entity.enums.OrderStatus;
import smu.nuda.domain.order.repository.OrderRepository;
import smu.nuda.domain.order.service.OrderService;
import smu.nuda.domain.payment.dto.PaymentCompleteRequest;
import smu.nuda.domain.payment.dto.PaymentCompleteResponse;
import smu.nuda.domain.payment.dto.PaymentRequestResponse;
import smu.nuda.domain.payment.entity.Payment;
import smu.nuda.domain.payment.entity.enums.PaymentStatus;
import smu.nuda.domain.payment.error.PaymentErrorCode;
import smu.nuda.domain.payment.repository.PaymentRepository;
import smu.nuda.global.error.DomainException;
import smu.nuda.support.member.MemberTestFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/*
    결제 시스템(Payment) 관련 비즈니스 로직 검증 테스트 클래스

    - 결제 요청 시 Payment 엔티티의 초기 상태(REQUESTED) 생성 검증
    - 외부 PG사 콜백 이후 결제 완료 처리 및 주문 상태 변경 로직 확인
    - 결제 금액 위조 방지 및 중복 결제 방지 정책 검증
    - 결제 성공 후 장바구니 연동(주문 상품 자동 삭제) 흐름 확인
 */

@SpringBootTest
@Transactional
public class PaymentServiceTest {

    @Autowired PaymentService paymentService;
    @Autowired CartRepository cartRepository;
    @Autowired CartItemRepository cartItemRepository;
    @Autowired PaymentRepository paymentRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired OrderService orderService;
    @Autowired MemberTestFactory memberTestFactory;
    @Autowired private EntityManager em;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberTestFactory.active();
    }

    private Order createPendingOrder(int totalAmount) {
        Order order = Order.create(
                member.getId(),
                299902080001L,
                totalAmount
        );

        return orderRepository.save(order);
    }

    @Test
    @DisplayName("결제 요청 성공 - PENDING 주문에 대해 Payment가 REQUESTED 상태로 생성된다")
    void requestPayment_success() {
        // [Given] 결제 대기 중인 주문
        Order order = createPendingOrder(88500);

        // [When] 결제 요청
        PaymentRequestResponse response = paymentService.requestPayment(member, order.getId());

        // [Then] 생성된 결제 데이터의 유효성 검증
        Payment payment = paymentRepository.findById(response.getPaymentId()).orElseThrow();

        assertThat(payment.getOrder().getId()).isEqualTo(order.getId());
        assertThat(payment.getAmount()).isEqualTo(order.getTotalAmount());
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REQUESTED); // 초기 상태는 REQUESTED
        assertThat(payment.getPaymentKey()).isNotNull();
    }

    @Test
    @DisplayName("이미 결제 요청된 주문에 대해 다시 요청하면 예외가 발생한다")
    void requestPayment_duplicate() {
        // [Given] 이미 한 번 결제 요청이 완료된 주문
        Order order = createPendingOrder(50000);
        paymentService.requestPayment(member, order.getId());

        // [When, Then] 동일한 주문으로 재요청 시 ALREADY_REQUESTED 예외 발생 확인
        assertThatThrownBy(() -> paymentService.requestPayment(member, order.getId()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(PaymentErrorCode.ALREADY_REQUESTED.getMessage());
    }

    @Test
    @DisplayName("결제 완료 성공 - Payment와 Order 상태가 성공으로 변경된다")
    void completePayment_success() {
        // [Given] 결제 요청 상태인 주문과 결제 키
        Order order = createPendingOrder(30000);
        PaymentRequestResponse response = paymentService.requestPayment(member, order.getId());
        Payment payment = paymentRepository.findById(response.getPaymentId()).orElseThrow();

        PaymentCompleteRequest request = new PaymentCompleteRequest(
                payment.getPaymentKey(),
                order.getId(),
                order.getTotalAmount(),
                true
        );

        // [When] PG사로부터 결제 완료 콜백 호출
        PaymentCompleteResponse result = paymentService.completePayment(member, request);

        // [Then] 결제 및 주문 상태가 최종 성공(SUCCESS, PAID)으로 변경되었는지 확인
        assertThat(result).isNotNull();
        assertThat(result.getOrderNum()).isEqualTo(order.getOrderNum());
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("이미 성공 처리된 결제에 대해 다시 콜백이 오면 예외가 발생한다")
    void completePayment_duplicateCallback() {
        // [Given] 이미 결제 완료 처리가 끝난 상태
        Order order = createPendingOrder(30000);
        PaymentRequestResponse response = paymentService.requestPayment(member, order.getId());
        Payment payment = paymentRepository.findById(response.getPaymentId()).orElseThrow();

        PaymentCompleteRequest request = new PaymentCompleteRequest(
                payment.getPaymentKey(),
                order.getId(),
                order.getTotalAmount(),
                true
        );
        paymentService.completePayment(member, request);

        // [When, Then] 중복된 완료 요청 시 ALREADY_PAID 예외 발생 확인
        assertThatThrownBy(() -> paymentService.completePayment(member, request))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(PaymentErrorCode.ALREADY_PAID.getMessage());
    }

    @Test
    @DisplayName("결제 금액이 다르면 결제 완료가 거부된다")
    void completePayment_invalidAmount() {
        // [Given] 결제 요청된 금액과 다른 위조된 요청 데이터
        Order order = createPendingOrder(30000);
        PaymentRequestResponse response = paymentService.requestPayment(member, order.getId());
        Payment payment = paymentRepository.findById(response.getPaymentId()).orElseThrow();

        PaymentCompleteRequest request = new PaymentCompleteRequest(
                payment.getPaymentKey(),
                order.getId(),
                100, // 위조된 금액
                true
        );

        // [When, Then] 금액 불일치(INVALID_AMOUNT) 검증 예외 발생 확인
        assertThatThrownBy(() -> paymentService.completePayment(member, request))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(PaymentErrorCode.INVALID_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("결제 완료 시 주문에 포함된 상품만 장바구니에서 삭제된다")
    void completePayment_removesOnlyOrderedCartItems() {
        // [Given] 회원의 장바구니에 상품 A 2개, 상품 B 1개
        Cart cart = cartRepository.save(new Cart(member.getId()));

        Long productA = 1L;
        Long productB = 51L;

        cart.addProduct(productA);
        cart.addProduct(productA);
        cart.addProduct(productB);
        cartRepository.save(cart);

        // [Given] 상품 A만 포함된 주문을 생성함
        OrderCreateRequest orderRequest = new OrderCreateRequest(List.of(new OrderItemRequest(productA, 2)));
        OrderCreateResponse orderResponse = orderService.createOrder(member, orderRequest);

        // 1차 캐시에 남은 엔티티 상태를 DB와 동기화하고 캐시를 비워 테스트 정밀도 확보
        em.flush();
        em.clear();

        // 결제 완료를 위한 요청 데이터 준비
        PaymentRequestResponse response = paymentService.requestPayment(member, orderResponse.getOrderId());
        Payment payment = paymentRepository.findById(response.getPaymentId()).orElseThrow();

        PaymentCompleteRequest request = new PaymentCompleteRequest(
                payment.getPaymentKey(),
                orderResponse.getOrderId(),
                orderResponse.getTotalAmount(),
                true
        );

        // [When] 최종 결제 완료 처리 수행
        paymentService.completePayment(member, request);

        // [Then] 장바구니에서 주문된 상품 A는 제거되고 주문되지 않은 상품 B만 남아있는지 확인
        List<CartItem> remainingItems = cartItemRepository.findByCart_MemberId(member.getId());
        assertThat(remainingItems).hasSize(1);
        assertThat(remainingItems.get(0).getProductId()).isEqualTo(productB);
    }

}
