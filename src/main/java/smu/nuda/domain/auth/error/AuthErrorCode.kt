package smu.nuda.domain.auth.error

import smu.nuda.global.error.ErrorCode

enum class AuthErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_CREDENTIALS(
        "AUTH_INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다"
    ),

    EMAIL_ALREADY_EXISTS(
        "AUTH_EMAIL_DUPLICATED", "이미 가입된 이메일입니다"
    )
}