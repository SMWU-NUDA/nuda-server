package smu.nuda.global.guard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.dto.DeliveryRequest;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.domain.signupdraft.repository.SignupDraftRepository;
import smu.nuda.domain.signupdraft.usecase.SignupDraftUseCase;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/*
    회원가입 단계(SignupStep) 정책이
    HTTP 요청 흐름에서 AOP 기반 Guard를 통해 일관되게 적용됨을 검증

    - 컨트롤러 내부에는 회원가입 단계 조건문이 존재하지 않음
    - 요청의 허용/차단은 중앙 집중화된 Guard 정책(SignupDraftPolicy)에 의해 통제됨
    - 실제 웹 요청 단계에서 Guard가 인터셉트하여 비즈니스 로직을 보호하는지 검증
*/

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignupDraftGuardTest {

    @Autowired MockMvc mockMvc;
    @Autowired SignupDraftRepository signupDraftRepository;
    @Autowired ObjectMapper objectMapper;
    @Autowired SignupDraftUseCase signupDraftUseCase;

    @Test
    @DisplayName("진행 단계가 일치하지 않는 API 호출 시 요청이 차단된다")
    void stepMismatch_fail() throws Exception {
        // [Given] 회원가입 임시 저장이 ACCOUNT 단계에 머물러 있는 경우
        SignupDraft draft = signupDraftRepository.save(
                SignupDraft.builder()
                        .signupToken("token-1")
                        .currentStep(SignupStep.ACCOUNT)
                        .build()
        );
        // [When] 다음 단계인 DELIVERY API를 호출했을 때
        // [Then] Guard가 단계를 검증하여 SIGNUP_DRAFT_NOT_COMPLETED 에러를 반환함
        mockMvc.perform(
                        put("/signup/draft/delivery")
                                .header("Signup-Token", "token-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"recipient\":\"test\", \"phoneNum\":\"01012345678\", \"postalCode\":\"12345\", \"address1\":\"addr1\", \"address2\":\"addr2\"}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("SIGNUP_DRAFT_NOT_COMPLETED"));
    }

    @Test
    @DisplayName("단계가 일치하면 배송 정보 입력에 성공한다")
    void stepMatch_success() throws Exception {
        // [Given] 회원가입 임시 저장이 DELIVERY 단계에 있는 경우
        signupDraftRepository.save(
                SignupDraft.builder()
                        .signupToken("token-2")
                        .currentStep(SignupStep.DELIVERY)
                        .build()
        );

        DeliveryRequest request = DeliveryRequest.builder()
                .recipient("받는사람")
                .phoneNum("01012345678")
                .postalCode("12345")
                .address1("서울시 강남구...")
                .address2("101호")
                .build();

        // [When] 현재 단계에 맞는 DELIVERY API를 호출했을 때
        // [Then] Guard를 통과하여 정상적으로 200 OK 응답힘
        mockMvc.perform(
                        put("/signup/draft/delivery")
                                .header("Signup-Token", "token-2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 완료 commit 이후 기존 Draft 토큰으로 접근 시 요청이 차단된다")
    void accessAfterCommit_fail() throws Exception {
        // [Given] 회원가입 마지막 단계인 COMPLETED 상태의 Draft 데이터 생성 및 commit 수행
        String token = "token-" + UUID.randomUUID().toString().substring(0, 8);

        signupDraftRepository.save(SignupDraft.builder()
                .signupToken(token)
                .currentStep(SignupStep.COMPLETED)
                .email(token + "@test.com")
                .username("user_" + token)
                .nickname("nick")
                .password("pass")
                .build());

        signupDraftUseCase.commit(token);

        // [When] 가입 완료된 토큰을 사용하여 다시 Draft API 접근을 시도했을 때
        // [Then] Guard가 DB에서 데이터를 찾지 못해 SIGNUP_DRAFT_NOT_FOUND 에러를 반환
        mockMvc.perform(
                        get("/signup/draft") // 보통 조회 API는 GET이므로 이를 활용
                                .header("Signup-Token", token)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SIGNUP_DRAFT_NOT_FOUND"));
    }
}
