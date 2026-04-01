package smu.nuda.global.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import smu.nuda.domain.auth.error.AuthErrorCode
import smu.nuda.global.error.ErrorCode
import smu.nuda.global.security.exception.JwtAuthenticationException

@Component
class CustomAuthenticationEntryPoint(
    private val securityErrorResponse: SecurityErrorResponse
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()

        val errorCode: ErrorCode = if (authException is JwtAuthenticationException) {
            authException.errorCode
        } else {
            AuthErrorCode.AUTH_REQUIRED
        }

        securityErrorResponse.write(response, errorCode)
    }
}
