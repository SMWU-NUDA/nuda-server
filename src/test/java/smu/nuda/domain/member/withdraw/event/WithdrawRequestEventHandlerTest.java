package smu.nuda.domain.member.withdraw.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import smu.nuda.domain.cart.repository.CartRepository;
import smu.nuda.domain.like.repository.BrandLikeRepository;
import smu.nuda.domain.like.repository.IngredientLikeRepository;
import smu.nuda.domain.like.repository.ProductLikeRepository;
import smu.nuda.domain.like.repository.ReviewLikeRepository;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("WithdrawRequestEventHandler 단위 테스트")
class WithdrawRequestEventHandlerTest {

    /*
    탈퇴 요청 이벤트 핸들러가 연관 데이터를 올바르게 삭제하는지 검증함

    - reviewLike, productLike, brandLike, ingredientLike, cart를 memberId 기준으로 삭제
    - 각 Repository의 deleteByMemberId가 정확히 한 번씩 호출됨을 증명
     */

    @Mock ReviewLikeRepository reviewLikeRepository;
    @Mock ProductLikeRepository productLikeRepository;
    @Mock BrandLikeRepository brandLikeRepository;
    @Mock IngredientLikeRepository ingredientLikeRepository;
    @Mock CartRepository cartRepository;

    @InjectMocks WithdrawRequestEventHandler handler;

    @Test
    @DisplayName("탈퇴 요청 이벤트 수신 시 리뷰 좋아요가 삭제된다")
    void handle_deletes_reviewLike() {
        // [given] 탈퇴 요청 이벤트
        Long memberId = 1L;
        WithdrawRequestedEvent event = new WithdrawRequestedEvent(memberId);

        // [when] 이벤트 핸들러 실행
        handler.handle(event);

        // [then] 리뷰 좋아요 삭제 호출됨
        verify(reviewLikeRepository).deleteByMemberId(memberId);
    }

    @Test
    @DisplayName("탈퇴 요청 이벤트 수신 시 상품 좋아요가 삭제된다")
    void handle_deletes_productLike() {
        // [given] 탈퇴 요청 이벤트
        Long memberId = 1L;
        WithdrawRequestedEvent event = new WithdrawRequestedEvent(memberId);

        // [when] 이벤트 핸들러 실행
        handler.handle(event);

        // [then] 상품 좋아요 삭제 호출됨
        verify(productLikeRepository).deleteByMemberId(memberId);
    }

    @Test
    @DisplayName("탈퇴 요청 이벤트 수신 시 브랜드 좋아요가 삭제된다")
    void handle_deletes_brandLike() {
        // [given] 탈퇴 요청 이벤트
        Long memberId = 1L;
        WithdrawRequestedEvent event = new WithdrawRequestedEvent(memberId);

        // [when] 이벤트 핸들러 실행
        handler.handle(event);

        // [then] 브랜드 좋아요 삭제 호출됨
        verify(brandLikeRepository).deleteByMemberId(memberId);
    }

    @Test
    @DisplayName("탈퇴 요청 이벤트 수신 시 성분 좋아요가 삭제된다")
    void handle_deletes_ingredientLike() {
        // [given] 탈퇴 요청 이벤트
        Long memberId = 1L;
        WithdrawRequestedEvent event = new WithdrawRequestedEvent(memberId);

        // [when] 이벤트 핸들러 실행
        handler.handle(event);

        // [then] 성분 좋아요 삭제 호출됨
        verify(ingredientLikeRepository).deleteByMemberId(memberId);
    }

    @Test
    @DisplayName("탈퇴 요청 이벤트 수신 시 장바구니가 삭제된다")
    void handle_deletes_cart() {
        // [given] 탈퇴 요청 이벤트
        Long memberId = 1L;
        WithdrawRequestedEvent event = new WithdrawRequestedEvent(memberId);

        // [when] 이벤트 핸들러 실행
        handler.handle(event);

        // [then] 장바구니 삭제 호출됨
        verify(cartRepository).deleteByMemberId(memberId);
    }
}
