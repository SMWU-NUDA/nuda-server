package smu.nuda.domain.member.withdraw.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WithdrawCompleteEventHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WithdrawCompletedEvent event) {

        Long memberId = event.getMemberId();

        // Todo. 연관 도메인 정리
        // productRepository.deleteByMemberId(memberId);
        // commentRepository.deleteByMemberId(memberId);
        // logRepository.deleteByMemberId(memberId);
    }
}
