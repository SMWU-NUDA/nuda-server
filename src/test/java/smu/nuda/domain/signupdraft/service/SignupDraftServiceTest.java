package smu.nuda.domain.signupdraft.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.domain.signupdraft.repository.SignupDraftRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
    회원가입 임시 저장 데이터(SignupDraft)의 만료 정책 서비스 검증

    - 데이터베이스의 expires_at 컬럼을 기준으로 만료된 데이터를 선별 삭제하는지 확인
    - 유효 기간이 남은 데이터는 시스템에 유지되어야 함을 보장
    - 서비스 레이어의 비즈니스 로직(Bulk Delete) 정확성을 검증하는 통합 테스트
*/

@SpringBootTest
@Transactional
public class SignupDraftServiceTest {

    @Autowired
    SignupDraftService signupDraftService;

    @Autowired
    SignupDraftRepository signupDraftRepository;

    @Test
    @DisplayName("24시간이 지난 SignupDraft만 만료 처리된다")
    void expireDrafts_onlyExpiredDraftsAreDeleted() {
        // [Given] 현재 시간을 기준으로 만료된 데이터와 유효한 데이터를 정의
        LocalDateTime now = LocalDateTime.now();

        // 만료 시간이 지남
        SignupDraft expiredDraft = SignupDraft.builder()
                .signupToken("expired-token")
                .password("dummy-pass")
                .currentStep(SignupStep.ACCOUNT)
                .expiresAt(now.minusHours(1))
                .build();

        // 유효 기간이 남음
        SignupDraft validDraft = SignupDraft.builder()
                .signupToken("valid-token")
                .password("dummy-pass")
                .currentStep(SignupStep.ACCOUNT)
                .expiresAt(now.plusHours(23))
                .build();

        signupDraftRepository.saveAll(List.of(expiredDraft, validDraft));

        // [When] 서비스의 만료 데이터 정리 로직을 실행
        int deletedCount = signupDraftService.expireDrafts();

        // [Then] 결과 검증
        // 삭제된 행의 개수가 정확히 1개인지 확인
        assertThat(deletedCount).isEqualTo(1);
        // DB에 유효한 데이터("valid-token")만 남아있는지 확인
        assertThat(signupDraftRepository.findAll())
                .hasSize(1)
                .extracting(SignupDraft::getSignupToken)
                .containsExactly("valid-token");
    }
}

