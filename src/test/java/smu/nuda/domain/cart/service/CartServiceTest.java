package smu.nuda.domain.cart.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import smu.nuda.domain.cart.entity.Cart;
import smu.nuda.domain.cart.entity.CartItem;
import smu.nuda.domain.cart.repository.CartItemRepository;
import smu.nuda.domain.cart.repository.CartRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/*
    장바구니 서비스의 핵심 비즈니스 로직 및 동시성 제어를 검증

    - 동일 상품 추가 시 기존 아이템의 수량 증가(increaseQuantity) 로직 검증
    - 락 경합 발생 시 타임아웃 예외 처리 및 시스템 안정성 확인
    - Race Condition 방지
*/

@Slf4j
@SpringBootTest
class CartServiceTest {

    @Autowired CartService cartService;
    @Autowired CartRepository cartRepository;
    @Autowired CartItemRepository cartItemRepository;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 같은 상품을 장바구니에 추가해도 수량이 정확히 합산된다")
    void addProduct_increases_quantity_correctly() throws Exception {

        // [given] 10개의 스레드가 동시에 memberId 1번의 장바구니에 productId 101번 상품을 담을 때
        Long memberId = 1L;
        Long productId = 101L;
        int threadCount = 10;
        cartRepository.save(new Cart(memberId));

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        // [when] 모든 스레드가 동시에 addProduct를 호출하여 경합 발생
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 동시에 시작하도록 대기
                    cartService.addProduct(memberId, productId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("Failed to add product: {}", e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // 모든 스레드 동시 시작
        doneLatch.await(); // 모든 스레드 완료 대기
        executorService.shutdown();

        // [then] 최종 장바구니 아이템의 수량이 스레드 수인 10과 일치함
        CartItem cartItem = cartItemRepository.findByMemberIdAndProductId(memberId, productId)
                .orElseThrow(() -> new AssertionError("CartItem not found"));
        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(cartItem.getQuantity()).isEqualTo(threadCount);

        // Race Condition이 발생하지 않음 -> CartItem이 중복 생성되지 않음
        long totalCartItems = cartItemRepository.count();
        assertThat(totalCartItems).isEqualTo(1L);
    }

    @Test
    @DisplayName("비관적 락 타임아웃 시 적절한 예외가 발생한다")
    void addProduct_lock_timeout_throws_proper_exception() throws Exception {
        // [given] 락 타임아웃을 유발하기 위해 50개의 대량 스레드 요청할 때
        Long memberId = 1L;
        Long productId = 101L;
        cartRepository.save(new Cart(memberId));

        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger timeoutCount = new AtomicInteger(0);

        // [when] 락 경합 중 설정된 시간을 초과한 요청에서 LOCK_TIMEOUT 예외가 발생
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    cartService.addProduct(memberId, productId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    if (e.getMessage() != null && e.getMessage().contains("LOCK_TIMEOUT")) {
                        timeoutCount.incrementAndGet();
                    }
                    log.debug("Thread exception: {}", e.getClass().getSimpleName());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();
        Thread.sleep(500);

        // [then] 일부 요청은 성공하여 수량이 반영되고, 나머지는 타임아웃 예외가 정상적으로 카운트됨
        log.info("Success: {}, Timeout: {}", successCount.get(), timeoutCount.get());

        assertThat(successCount.get()).isGreaterThan(0);
        assertThat(successCount.get() + timeoutCount.get()).isEqualTo(threadCount);

        CartItem cartItem = cartItemRepository.findByMemberIdAndProductId(memberId, productId)
                .orElseThrow();
        assertThat(cartItem.getQuantity()).isEqualTo(successCount.get());
    }

}
