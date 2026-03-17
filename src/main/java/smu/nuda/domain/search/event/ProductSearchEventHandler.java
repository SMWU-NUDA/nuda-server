package smu.nuda.domain.search.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.nuda.domain.search.service.ProductSearchService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchEventHandler {

    private final ProductSearchService productSearchService;

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductIndexing(ProductIndexingEvent event) {
        log.info("[ES Indexing] {}개 상품 인덱싱 시작", event.docs().size());
        productSearchService.indexAll(event.docs());
        log.info("[ES Indexing] {}개 상품 인덱싱 완료", event.docs().size());
    }
}
