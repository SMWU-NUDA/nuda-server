package smu.nuda.global.cache.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.nuda.domain.review.event.ReviewUpdateEvent;

@Component
@RequiredArgsConstructor
public class MlReviewCacheEvictHandler {

    private final MlReviewCacheFacade mlReviewCacheFacade;

    // 리뷰 변경 시 리뷰 ai 요약 캐시 제거
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReviewUpdateEvent event) {
        mlReviewCacheFacade.evictReviewAiSummary(event.productId());
    }
}
