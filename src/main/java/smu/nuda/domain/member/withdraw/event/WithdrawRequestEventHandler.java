package smu.nuda.domain.member.withdraw.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.nuda.domain.cart.repository.CartRepository;
import smu.nuda.domain.like.repository.BrandLikeRepository;
import smu.nuda.domain.like.repository.IngredientLikeRepository;
import smu.nuda.domain.like.repository.ProductLikeRepository;
import smu.nuda.domain.like.repository.ReviewLikeRepository;

@Component
@RequiredArgsConstructor
public class WithdrawRequestEventHandler {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ProductLikeRepository productLikeRepository;
    private final BrandLikeRepository brandLikeRepository;
    private final IngredientLikeRepository ingredientLikeRepository;
    private final CartRepository cartRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WithdrawRequestedEvent event) {
        Long memberId = event.getMemberId();

        reviewLikeRepository.deleteByMemberId(memberId);
        productLikeRepository.deleteByMemberId(memberId);
        brandLikeRepository.deleteByMemberId(memberId);
        ingredientLikeRepository.deleteByMemberId(memberId);
        cartRepository.deleteByMemberId(memberId);  // DB cascade -> cart_item 자동 삭제

    }
}
