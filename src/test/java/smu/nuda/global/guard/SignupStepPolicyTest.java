package smu.nuda.global.guard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.SignupStepType;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.guard.guard.SignupGuard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class SignupStepPolicyTest {

    /*
    회원가입 완료 여부(SignupStepType)를 도메인 정책으로 검증

    - 회원가입 완료는 요청 맥락이 아닌 Member 상태로 결정됨
    - Spring, Security, Web 계층과 무관한 불변 규칙임을 증명
    - Guard, AOP, Controller에서 재사용 가능한 정책의 기준점 역할
    */

    private final SignupGuard signupGuard = new SignupGuard();

    @Test
    @DisplayName("회원가입이 완료되지 않은 상태면 예외가 발생한다")
    void signupStepEnsureCompleted_fail() {
        // [given] 회원가입 단계가 SIGNUP 상태인 회원
        Member member = Member.builder()
                .signupStep(SignupStepType.SIGNUP)
                .build();

        // [when, then] 회원가입 완료 조건을 만족하지 않아 예외 발생
        assertThatThrownBy(() -> signupGuard.ensureCompleted(member))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("회원가입이 완료되지 않았습니다.");
    }
}
