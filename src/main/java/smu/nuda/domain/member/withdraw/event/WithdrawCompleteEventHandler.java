package smu.nuda.domain.member.withdraw.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import smu.nuda.domain.auth.repository.RefreshTokenRepository;

@Component
@RequiredArgsConstructor
public class WithdrawCompleteEventHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WithdrawCompletedEvent event) {
        refreshTokenRepository.delete(event.getMemberId());
    }
}
