package smu.nuda.global.guard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.SignupStepType;
import smu.nuda.global.guard.guard.AuthenticationGuard;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SignupStepGuardTest {

    /*
    회원가입 단계(SignupStepType) 정책이
    HTTP 요청 흐름에서 AOP 기반 Guard를 통해 일관되게 적용됨을 검증

    - 컨트롤러에는 회원가입 단계 조건문이 존재하지 않음을 증명
    - 요청 허용/차단은 Guard 정책에 의해 중앙 통제됨
    - 정책 적용 시점(웹 요청 단계)을 검증하는 통합 테스트
    */

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthenticationGuard authenticationGuard;

    @Test
    @WithMockUser
    void signupStepMismatch_fail() throws Exception {
        // [given] 현재 signupStep = SIGNUP
        Member member = Member.builder()
                .signupStep(SignupStepType.SIGNUP)
                .build();

        given(authenticationGuard.currentMember())
                .willReturn(member);

        // [when, then] DELIVERY 단계 API 호출시 SIGNUP_STEP_REQUIRED 예외 발생
        mockMvc.perform(get("/test/auth/delivery"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("MEMBER_SIGNUP_STEP_REQUIRED"));
    }

    @Test
    @WithMockUser
    void signupStepMatch_success() throws Exception {
        // [given] 현재 signupStep = DELIVERY
        Member member = Member.builder()
                .signupStep(SignupStepType.DELIVERY)
                .build();

        given(authenticationGuard.currentMember())
                .willReturn(member);

        // [when, then] DELIVERY 단계 API 호출시 정상 통과
        mockMvc.perform(get("/test/auth/delivery"))
                .andExpect(status().isOk());
    }
}
