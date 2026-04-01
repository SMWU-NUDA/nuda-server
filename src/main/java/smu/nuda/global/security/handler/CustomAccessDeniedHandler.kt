package smu.nuda.global.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import smu.nuda.domain.auth.error.AuthErrorCode
import smu.nuda.global.error.ErrorCode

@Component
class CustomAccessDeniedHandler(
    private val securityErrorResponse: SecurityErrorResponse
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.status = HttpStatus.FORBIDDEN.value()

        val errorCode = request.getAttribute("POLICY_VIOLATION") as? ErrorCode
            ?: AuthErrorCode.ACCESS_DENIED

        securityErrorResponse.write(response, errorCode)
    }
}
