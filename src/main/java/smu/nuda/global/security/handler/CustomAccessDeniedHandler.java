package smu.nuda.global.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.global.error.ErrorCode;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorResponse securityErrorResponse;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ErrorCode errorCode = resolveErrorCode(request, accessDeniedException);
        securityErrorResponse.write(response, errorCode);
    }

    /*
    인가 실패 사유를 해석
     - 기본값은 ACCESS_DENIED
     - Guard/Policy에서 ErrorCode를 POLICY_VIOLATION에 담아 던지면
       Security가 403 + ApiResponse로 표현
     */
    private ErrorCode resolveErrorCode(HttpServletRequest request, AccessDeniedException exception) {
        // Guard/Policy에서 사용할 슬롯
        Object policyViolation = request.getAttribute("POLICY_VIOLATION");

        if (policyViolation instanceof ErrorCode errorCode) {
            return errorCode;
        }

        // 기본 인가 실패
        return AuthErrorCode.ACCESS_DENIED;
    }
}
