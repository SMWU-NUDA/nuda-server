package smu.nuda.domain.product.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.nuda.domain.review.event.ReviewUpdateEvent;

@Component
@RequiredArgsConstructor
public class ProductCacheEvictHandler {

    private final ProductCacheFacade productCacheFacade;

    // 리뷰 변경 시 상품 상세 캐시 제거
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReviewUpdateEvent event) {
        productCacheFacade.evictProductDetail(event.productId());
    }
}
