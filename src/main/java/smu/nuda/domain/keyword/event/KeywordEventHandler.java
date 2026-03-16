package smu.nuda.domain.keyword.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.nuda.global.ml.orchestrator.MlOrchestrator;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeywordEventHandler {

    private final MlOrchestrator mlOrchestrator;

    @Async("mlExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleKeywordUpdated(KeywordUpdateEvent event) {

        log.info("[EventHandler] KeywordUpdatedEvent received - memberId={}", event.payload().memberId());

        mlOrchestrator.handleKeywordChanged(event.payload());
    }
}
