package smu.nuda.domain.member.withdraw.policy;

import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.global.error.DomainException;

/*
 활성 상태 회원만 탈퇴를 허용하는 정책
 - ACTIVE 상태가 아니면 탈퇴 불가
*/
@Component
public class ActiveMemberWithdrawGuard implements WithdrawGuard {

    @Override
    public void check(Member member) {
        if (member.getStatus() != Status.ACTIVE) {
            throw new DomainException(MemberErrorCode.INVALID_STATUS);
        }
    }
}
