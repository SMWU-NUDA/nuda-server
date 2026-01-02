package smu.nuda.domain.member.withdraw.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WithdrawRequestEventHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WithdrawRequestedEvent event) {

        Long memberId = event.getMemberId();

        // Todo. 토큰/세션 무효화
        // tokenService.invalidate(memberId);

        // Todo. 탈퇴 로그 기록
        // withdrawLogService.save(memberId);

        // Todo. 통계/분석 처리
        // analyticsService.trackWithdraw(memberId);
    }
}
