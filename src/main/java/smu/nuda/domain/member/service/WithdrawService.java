package smu.nuda.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.withdraw.WithdrawPolicyExecutor;
import smu.nuda.domain.member.withdraw.event.WithdrawRequestedEvent;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.guard.guard.AuthenticationGuard;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class WithdrawService {

    private final WithdrawPolicyExecutor withdrawPolicyExecutor;
    private final AuthenticationGuard authenticationGuard;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional
    public void withdraw() {
        Member member = authenticationGuard.currentMember();

        // 탈퇴 정책 검증
        withdrawPolicyExecutor.validate(member);

        // soft delete -> deletedAt, status = WITHDRAW_REQUESTED
        member.requestWithdraw(clock);

        // 좋아요, 장바구니 즉시 삭제 이벤트 발행
        eventPublisher.publishEvent(new WithdrawRequestedEvent(member.getId()));
    }

    @Transactional
    public void cancelWithdraw() {
        Member member = authenticationGuard.currentMember();

        if (member.getStatus() != Status.WITHDRAW_REQUESTED) {
            throw new DomainException(MemberErrorCode.NOT_IN_WITHDRAW_REQUESTED);
        }
        if (!member.isWithinCancellationWindow(clock)) {
            throw new DomainException(MemberErrorCode.CANCELLATION_WINDOW_EXPIRED);
        }

        member.cancelWithdraw();
    }
}
