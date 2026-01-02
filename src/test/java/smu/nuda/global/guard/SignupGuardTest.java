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
public class SignupGuardTest {

    /*
    회원가입 단계(SignupStepType) 검증이 컨트롤러에 존재하지 않음을 증명함

    회원가입 단계 정책을 AOP 기반 Guard로 중앙화하고
    컨트롤러는 단계 조건을 선언적 어노테이션으로 표현하기만 하면
    허용/차단은 공통 정책에 의해 일관되게 처리됨
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
                        .value("SIGNUP_STEP_REQUIRED"));
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
