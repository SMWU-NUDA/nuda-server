package smu.nuda.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.withdraw.WithdrawPolicyExecutor;
import smu.nuda.global.guard.guard.AuthenticationGuard;

@Service
@RequiredArgsConstructor
public class WithdrawService {

    private final WithdrawPolicyExecutor withdrawPolicyExecutor;
    private final AuthenticationGuard authenticationGuard;

    @Transactional
    public void withdraw() {
        Member member = authenticationGuard.currentMember();

        // 탈퇴 정책 검증
        withdrawPolicyExecutor.validate(member);

        member.requestWithdraw();
    }
}
