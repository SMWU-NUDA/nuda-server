package smu.nuda.global.security.exception

import org.springframework.security.core.AuthenticationException
import smu.nuda.global.error.ErrorCode

class JwtAuthenticationException @JvmOverloads constructor(
    val errorCode: ErrorCode,
    cause: Throwable? = null
) : AuthenticationException(errorCode.message, cause)
