package smu.nuda.global.cache.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.nuda.domain.keyword.event.KeywordUpdateEvent;

@Component
@RequiredArgsConstructor
public class MlProductCacheEvictHandler {

    private final MlProductCacheFacade mlProductCacheFacade;

    // 키워드 변경 시 맞춤 상품 랭킹 캐시 제거
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(KeywordUpdateEvent event) {
        mlProductCacheFacade.evictPersonalRanking(event.payload().memberId());
    }
}