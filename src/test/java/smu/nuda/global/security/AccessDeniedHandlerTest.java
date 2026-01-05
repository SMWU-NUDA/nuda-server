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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccessDeniedHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTestFactory jwtTestFactory;

    @Test
    @DisplayName("권한이 없는 요청시 AccessDeniedHandler에서 403을 반환한다")
    void accessDenied_403() throws Exception {
        // [given] USER 권한을 가진 정상 Access Token
        String token = jwtTestFactory.activeAccessToken();

        // [when, then] ADMIN 전용 API 접근 시 AUTH_ACCESS_DENIED(403) 발생
        mockMvc.perform(
                        get("/admin/secret")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_ACCESS_DENIED"));
    }
}
