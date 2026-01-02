package smu.nuda.domain.member.withdraw.policy;

import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.global.error.DomainException;

@Component
public class WithdrawCooldownGuard implements WithdrawGuard {
    @Override
    public void check(Member member) {
        if (member.isWithinWithdrawCooldown()) {
            throw new DomainException(MemberErrorCode.WITHDRAW_COOLDOWN);
        }
    }
}
