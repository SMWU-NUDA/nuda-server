package smu.nuda.domain.log.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.nuda.domain.log.entity.CommerceLog;
import smu.nuda.domain.log.repository.CommerceLogRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommerceEventHandler {

    private final CommerceLogRepository commerceLogRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CommerceEvent event) {

        CommerceLog commerceLog = new CommerceLog(
                null,
                event.memberId(),
                event.productId(),
                event.externalProductId(),
                event.commerceType(),
                event.quantity(),
                LocalDateTime.now()
        );

        try {
            commerceLogRepository.save(commerceLog);
        } catch (Exception e) {
            log.error("CommerceEvent 저장 실패", e);
        }

        // Kafka publish 확장성 보장
    }
}
