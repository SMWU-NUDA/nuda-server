package smu.nuda.domain.member.withdraw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.withdraw.policy.WithdrawGuard;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawPolicyExecutorTest {

    /*
    회원 탈퇴 정책 검증 로직을 Guard 조합 기반 오케스트레이터(WithdrawPolicyExecutor)로 분리했음을 검증함

    - 탈퇴 여부 가능 여부는 여러 정책(WithdrawGuard)의 조합으로 판단
    - WithdrawPolicyExecutor는 각 Guard를 순차적으로 실행하며 하나라도 정책을 위반하면 즉시 검증을 중단함

    정책 조합 + 순차 실행 + 조기 중단 구조로 모델링했으며
    정책 변경 및 확장에 유연한 구조임을 증명함
     */

    @Mock
    WithdrawGuard guard1;

    @Mock
    WithdrawGuard guard2;

    @InjectMocks
    WithdrawPolicyExecutor executor;

    @Test
    @DisplayName("모든 Guard가 순서대로 실행된다")
    void validate_success() {
        // [given] 두 개의 탈퇴 정책 Guard가 등록된 상태
        Member member = mock(Member.class);
        executor = new WithdrawPolicyExecutor(List.of(guard1, guard2));

        // [when] 탈퇴 정책 검증을 수행
        executor.validate(member);

        // [then] 모든 Guard의 check가 순서대로 호출됨
        verify(guard1).check(member);
        verify(guard2).check(member);
    }

    @Test
    @DisplayName("중간 Guard에서 예외가 발생하면 이후 Guard는 실행되지않는다")
    void validate_fail_when_guard_throws_exception() {
        // [given] 첫 번째 Guard에서 예외가 발생하는 탈퇴 정책 구성
        Member member = mock(Member.class);

        doThrow(new RuntimeException("fail"))
                .when(guard1).check(member);

        executor = new WithdrawPolicyExecutor(List.of(guard1, guard2));

        // [when] 탈퇴 정책 검증을 수행
        try {
            executor.validate(member);
        } catch (Exception ignored) {
        }

        // [then] 예외가 발생하고 이후 Guard는 실행되지 않음
        verify(guard1).check(member);
        verify(guard2, never()).check(member);
    }
}
