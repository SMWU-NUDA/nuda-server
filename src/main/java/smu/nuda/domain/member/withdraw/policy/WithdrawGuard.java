package smu.nuda.domain.member.withdraw.policy;

import smu.nuda.domain.member.entity.Member;

public interface WithdrawGuard {
    void check(Member member);
}
