package smu.nuda.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.domain.member.withdraw.WithdrawPolicyExecutor;
import smu.nuda.domain.member.withdraw.event.WithdrawCompletedEvent;
import smu.nuda.domain.member.withdraw.event.WithdrawRequestedEvent;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.guard.guard.AuthenticationGuard;

@Service
@RequiredArgsConstructor
public class WithdrawService {

    private final WithdrawPolicyExecutor withdrawPolicyExecutor;
    private final AuthenticationGuard authenticationGuard;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberRepository memberRepository;

    @Transactional
    public void withdraw() {
        Member member = authenticationGuard.currentMember();

        // 탈퇴 정책 검증
        withdrawPolicyExecutor.validate(member);

        member.requestWithdraw();

        // 탈퇴 요청 이벤트 발행
        eventPublisher.publishEvent(new WithdrawRequestedEvent(member.getId()));
    }

    @Transactional
    public void completeWithdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 탈퇴 완료 이벤트 발행
        eventPublisher.publishEvent(new WithdrawCompletedEvent(memberId));

        memberRepository.delete(member);
    }
}
