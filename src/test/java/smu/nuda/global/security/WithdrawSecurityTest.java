package smu.nuda.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.auth.dto.LoginRequest;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.support.auth.JwtTestFactory;
import smu.nuda.support.member.MemberTestFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("탈퇴 상태 Security 필터 동작 테스트")
class WithdrawSecurityTest {

    /*
    JwtAuthenticationFilter 내 탈퇴 상태 차단 로직을 검증하는 Security 통합 테스트

    - WITHDRAWN 멤버의 JWT로 일반 API 접근 → 401 MEMBER_WITHDRAWN
    - WITHDRAW_REQUESTED 멤버의 JWT로 일반 API 접근 → 401 MEMBER_WITHDRAW_IN_PROGRESS
    - WITHDRAW_REQUESTED 멤버의 JWT로 탈퇴 취소 API 접근 → 허용 (200)
    - WITHDRAWN 멤버의 로그인 시도 → 400 MEMBER_WITHDRAWN
     */

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTestFactory jwtTestFactory;

    @Autowired
    MemberTestFactory memberTestFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("WITHDRAWN 멤버 토큰으로 일반 API 요청 시 MEMBER_WITHDRAWN(401)을 반환한다")
    void withdrawn_member_blocked_from_api() throws Exception {
        // [given] WITHDRAWN 상태의 멤버 토큰
        String token = jwtTestFactory.withdrawnAccessToken();

        // [when, then] 일반 API 요청 차단 → 401
        mockMvc.perform(
                        get("/members/me")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("MEMBER_WITHDRAWN"));
    }

    @Test
    @DisplayName("WITHDRAW_REQUESTED 멤버 토큰으로 일반 API 요청 시 MEMBER_WITHDRAW_IN_PROGRESS(401)을 반환한다")
    void withdraw_requested_member_blocked_from_api() throws Exception {
        // [given] WITHDRAW_REQUESTED 상태의 멤버 토큰
        String token = jwtTestFactory.withdrawRequestedAccessToken();

        // [when, then] 탈퇴 취소 API 외 요청 차단 → 401
        mockMvc.perform(
                        get("/members/me")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("MEMBER_WITHDRAW_IN_PROGRESS"));
    }

    @Test
    @DisplayName("WITHDRAW_REQUESTED 멤버 토큰으로 탈퇴 취소 API 요청 시 허용된다")
    void withdraw_requested_member_can_cancel_withdraw() throws Exception {
        // [given] WITHDRAW_REQUESTED 상태의 멤버 (취소 가능 기간 내)
        String token = jwtTestFactory.withdrawRequestedAccessToken();

        // [when, then] DELETE /members/withdraw 는 허용됨
        mockMvc.perform(
                        delete("/members/withdraw")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("WITHDRAWN 멤버가 로그인 시도 시 MEMBER_WITHDRAWN(400)을 반환한다")
    void withdrawn_member_cannot_login() throws Exception {
        // [given] WITHDRAWN 상태의 멤버
        Member member = memberTestFactory.withdrawn();

        LoginRequest loginRequest = new LoginRequest();
        setField(loginRequest, "username", member.getUsername());
        setField(loginRequest, "password", MemberTestFactory.RAW_PASSWORD);

        // [when, then] 로그인 차단 → 400
        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MEMBER_WITHDRAWN"));
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
