package smu.nuda.global.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ErrorCode errorCode = AuthErrorCode.ACCESS_DENIED;

        Object policyViolation = request.getAttribute("POLICY_VIOLATION");
        if (policyViolation instanceof ErrorCode) {
            errorCode = (ErrorCode) policyViolation;
        }

        securityErrorResponse.write(response, errorCode);
    }

}
