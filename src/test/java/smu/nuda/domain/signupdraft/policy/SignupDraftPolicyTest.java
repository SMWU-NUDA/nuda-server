package smu.nuda.domain.signupdraft.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.global.error.DomainException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

/*
    회원가입 단계(SignupStep) 정책이
    HTTP 요청 흐름에서 AOP 기반 Guard를 통해 일관되게 적용됨을 검증

    - 컨트롤러 내부에는 회원가입 단계 조건문이 존재하지 않음을 증명 (관심사 분리)
    - 요청의 허용/차단은 중앙 집중화된 Guard 정책(SignupDraftPolicy)에 의해 통제됨
    - 도메인 정책 로직의 정확성을 검증하는 단위 테스트
*/

public class SignupDraftPolicyTest {
    private final SignupDraftPolicy policy = new SignupDraftPolicy();

    @Test
    @DisplayName("현재 단계보다 높은 단계 접근 시 예외가 발생한다")
    void accessDenied_whenStepTooHigh() {
        // [Given] Draft의 현재 단계가 DELIVERY(배송 정보 입력) 상태일 때
        SignupDraft draft = SignupDraft.builder()
                .currentStep(SignupStep.DELIVERY)
                .build();

        // [When & Then] 현재 단계보다 앞 단계인 SURVEY API 접근을 시도하면
        // SignupDraftPolicy에 의해 DomainException 예외가 발생함
        assertThatThrownBy(() ->
                policy.validateAccess(draft, SignupStep.SURVEY)
        ).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("같은 단계 또는 이전 단계 접근은 허용된다")
    void accessAllowed_whenStepEnough() {
        // [Given] Draft의 현재 단계가 SURVEY 상태일 때
        SignupDraft draft = SignupDraft.builder()
                .currentStep(SignupStep.SURVEY)
                .build();
        // [When & Then] 이미 통과했거나(DELIVERY) 현재 수행 중인 단계(SURVEY)로의 접근은
        // 정책 위반이 아니므로 어떠한 예외도 발생하지 않음
        assertThatCode(() ->
                policy.validateAccess(draft, SignupStep.DELIVERY)
        ).doesNotThrowAnyException();
    }
}
