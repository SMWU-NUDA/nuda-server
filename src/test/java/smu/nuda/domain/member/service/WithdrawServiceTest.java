package smu.nuda.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.withdraw.WithdrawPolicyExecutor;
import smu.nuda.global.guard.guard.AuthenticationGuard;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawServiceTest {

    /*
    회원 탈퇴 유스케이스가 올바르게 조립되었음을 검증함

    - 인증 Guard를 통해 현재 회원을 조회
    - WithdrawPolicyExecutor를 통해 탈퇴 가능 여부를 검증
    - 정책 검증 통과 후 Member 엔티티의 상태 전이(requestWithdraw)를 요청함

    본 테스트는 개별 정책이나 상태 전이 로직을 검증하지 않고
    유스케이스 흐름의 조립 책임이 WithdrawService에 있음을 증명함
     */

    @Mock
    AuthenticationGuard authenticationGuard;

    @Mock
    WithdrawPolicyExecutor withdrawPolicyExecutor;

    @InjectMocks
    WithdrawService withdrawService;

    @Test
    @DisplayName("회원 탈퇴 요청 시 인증 → 정책 검증 → 상태 전이가 순차적으로 수행된다")
    void withdraw_success() {
        // [given] 인증된 회원이 존재하고 탈퇴 정책 검증이 통과되는 상황
        Member member = mock(Member.class);

        when(authenticationGuard.currentMember())
                .thenReturn(member);

        // [when] 회원 탈퇴 요청을 수행
        withdrawService.withdraw();

        // [then] 인증 → 정책 검증 → 상태 전이 요청이 순차적으로 호출됨
        verify(authenticationGuard).currentMember();
        verify(withdrawPolicyExecutor).validate(member);
        verify(member).requestWithdraw();
    }
}
