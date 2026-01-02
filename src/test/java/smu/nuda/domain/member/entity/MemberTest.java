package smu.nuda.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import smu.nuda.domain.member.entity.enums.SignupStepType;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    /*
    회원가입을 단계 기반 상태 머신(SignupStepType)으로 모델링했음을 검증함

    - 배송지 입력 → 설문 완료 → 회원가입 완료는 명확한 단계 전이이며
    - completeSignup()은 이후 인증 흐름(자동 로그인)을 정당화하는 경계 이벤트로 동작함

    즉, 회원가입의 현재 단계와
    허용되는 다음 단계 전이를
    회원 엔티티 스스로 책임지는 모델임을 증명함
     */

    @Test
    @DisplayName("배송지 입력 완료 시 회원가입 단계가 DELIVERY로 변경된다")
    void completeDelivery_success() {
        // [given] 회원가입 단계 = SIGNUP
        Member member = Member.builder()
                .signupStep(SignupStepType.SIGNUP)
                .build();

        // [when] 배송지 입력 완료
        member.completeDelivery();

        // [then] signupStep = DELIVERY
        assertThat(member.getSignupStep()).isEqualTo(SignupStepType.DELIVERY);
    }

    @Test
    @DisplayName("설문 완료 시 회원가입 단계가 SURVEY로 변경된다")
    void completeSurvey_success() {
        // [given] 회원가입 단계 = DELIVERY
        Member member = Member.builder()
                .signupStep(SignupStepType.DELIVERY)
                .build();

        // [when] 설문 완료
        member.completeSurvey();

        // [then] signupStep = SURVEY
        assertThat(member.getSignupStep()).isEqualTo(SignupStepType.SURVEY);
    }

    @Test
    @DisplayName("회원가입 완료 시 signupStep은 COMPLETED로 변경된다")
    void completeSignup_success() {
        // [given] 회원가입 진행 중
        Member member = Member.builder()
                .signupStep(SignupStepType.SURVEY)
                .build();

        // [when] completeSignup()
        member.completeSignup();

        // [then] signupStep = COMPLETED
        assertThat(member.getSignupStep()).isEqualTo(SignupStepType.COMPLETED);
    }

}
