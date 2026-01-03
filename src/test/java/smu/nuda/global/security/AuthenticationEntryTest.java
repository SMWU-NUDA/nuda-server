package smu.nuda.global.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.support.auth.JwtTestFactory;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationEntryTest {

    /*
    JWT 인증 필터 및 Security EntryPoint 동작을 검증하는 테스트

    - 인증 실패(401)와 인가 실패(403)를 명확히 구분함
    - 기술적 JWT 예외는 AuthenticationEntryPoint에서 일관된 응답으로 처리됨을 증명
    - 컨트롤러 로직과 무관하게 Security 계층에서 요청이 차단됨을 보장
    */

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTestFactory jwtTestFactory;

    @Test
    @DisplayName("만료된 토큰으로 요청 시 AuthenticationEntryPoint에서 401을 반환한다")
    void expiredToken_401() throws Exception {
        // [given] 만료된 Access Token
        String expiredToken = jwtTestFactory.expiredAccessToken();

        // [when, then] 인증이 필요한 API 요청시 AUTH_EXPIRED_TOKEN(401) 발생
        mockMvc.perform(
                        get("/auth/logout")
                                .header("Authorization", "Bearer " + expiredToken)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_EXPIRED_TOKEN"));
    }

    @Test
    @DisplayName("비활성 계정 토큰으로 요청 시 AuthenticationEntryPoint에서 401를 반환한다")
    void inactiveAccount_401() throws Exception {
        // [given] INACTIVE 상태의 Member의 정상 토큰
        String inactiveToken = jwtTestFactory.inactiveAccessToken();

        // [when, then] 인증이 필요한 API 요청시 MEMBER_ACCOUNT_DISABLED(401) 발생
        mockMvc.perform(
                        get("/members/me/delivery")
                                .header("Authorization", "Bearer " + inactiveToken)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("MEMBER_ACCOUNT_DISABLED"));
    }

    @Test
    @DisplayName("Authorization 헤더 없이 요청시 AuthenticationEntryPoint에서 401를 반환한다")
    void noAuthorizationHeader_401() throws Exception {
        // [given] Authorization 헤더가 없는 요청

        // [when, then] 인증이 필요한 API 요청 시 AUTH_REQUIRED(401) 발생
        mockMvc.perform(get("/members/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_REQUIRED"));
    }

    @Test
    @DisplayName("Bearer 형식 아닌 토큰으로 요청시 AuthenticationEntryPoint에서 401를 반환한다")
    void invalidBearerFormat_401() throws Exception {
        // [given] Bearer 형식이 아닌 Authorization 헤더

        // [when, then] 인증 정보로 인식되지 않아 AUTH_REQUIRED(401) 발생
        mockMvc.perform(
                        get("/members/me")
                                .header("Authorization", "Basic abc.def.ghi")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_REQUIRED"));
    }

    @Test
    @DisplayName("위조된 토큰으로 요청시 AuthenticationEntryPoint에서 401를 반환한다")
    void invalidToken_401() throws Exception {
        // [given] 서명이 위조된 JWT 토큰

        // [when, then] 토큰 검증 실패로 AUTH_INVALID_ACCESS_TOKEN(401) 발생
        mockMvc.perform(
                        get("/members/me")
                                .header("Authorization", "Bearer invalid.token.value")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_INVALID_ACCESS_TOKEN"));
    }

}
