package smu.nuda.global.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.global.error.ErrorCode;
import smu.nuda.global.security.exception.JwtAuthenticationException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorResponse securityErrorResponse;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorCode errorCode = resolveErrorCode(authException);
        securityErrorResponse.write(response, errorCode);
    }

    /*
    AuthenticationException으로부터 ErrorCode를 추출
     - JwtAuthenticationException이면 내부 ErrorCode 사용
     - 그 외 경우 AUTH_REQUIRED로 fallback
     */
    private ErrorCode resolveErrorCode(AuthenticationException exception) {
        if (exception instanceof JwtAuthenticationException jwtException) {
            return jwtException.getErrorCode();
        }

        return AuthErrorCode.AUTH_REQUIRED;
    }
}
