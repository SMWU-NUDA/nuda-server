package smu.nuda.domain.member.withdraw;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.withdraw.policy.WithdrawGuard;

import java.util.List;

/*
회원 탈퇴 정책 실행자
- Bean 등록된 WithdrawGuard를 순차적으로 실행
- 각 정책은 서로의 존재, 내용, 개수에 대해 알지 못함 -> 독립적으로 분리
 */
@Component
@RequiredArgsConstructor
public class WithdrawPolicyExecutor {

    private final List<WithdrawGuard> withdrawGuards;

    public void validate(Member member) {
        for (WithdrawGuard guard : withdrawGuards) {
            guard.check(member);
        }
    }
}

