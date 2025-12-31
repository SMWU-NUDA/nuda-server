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
    ),
    EMAIL_VERIFICATION_EXPIRED(
        "AUTH_EMAIL_VERIFICATION_EXPIRED", "인증번호가 만료되었습니다"
    ),
    EMAIL_VERIFICATION_MISMATCH(
        "AUTH_EMAIL_VERIFICATION_MISMATCH", "인증번호가 일치하지 않습니다"
    ),
    EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS(
        "AUTH_EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS", "인증번호 입력 횟수를 초과했습니다. 인증번호를 다시 요청해주세요."
    ),
    EMAIL_NOT_VERIFIED(
        "AUTH_EMAIL_NOT_VERIFIED", "이메일 인증이 완료되지 않았습니다"
    ),
    MEMBER_NOT_ACTIVE(
        "AUTH_MEMBER_NOT_ACTIVE", "회원가입이 완료되지 않은 회원입니다"
    ),
    INVALID_ACCESS_TOKEN(
        "AUTH_INVALID_ACCESS_TOKEN", "유효하지 않은 액세스 토큰입니다"
    ),
    EXPIRED_ACCESS_TOKEN(
        "AUTH_EXPIRED_ACCESS_TOKEN", "액세스 토큰이 만료되었습니다"
    ),
    INVALID_REFRESH_TOKEN(
        "AUTH_INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다"
    ),
    EXPIRED_REFRESH_TOKEN(
        "AUTH_EXPIRED_REFRESH_TOKEN", "리프레시 토큰이 만료되었습니다"
    ),
    INVALID_SIGNUP_TOKEN(
        "AUTH_INVALID_SIGNUP_TOKEN", "유효하지 않은 회원가입 토큰입니다"
    ),
    EXPIRED_SIGNUP_TOKEN(
        "AUTH_EXPIRED_SIGNUP_TOKEN", "회원가입 토큰이 만료되었습니다"
    ),
    JWT_CONFIGURATION_NOT_FOUND(
        "AUTH_JWT_CONFIGURATION_NOT_FOUND", "JWT 토큰 설정이 존재하지 않습니다"
    ),
    INVALID_SIGNUP_FLOW(
        "INVALID_SIGNUP_FLOW", "회원가입 진행 중인 사용자만 접근할 수 있는 요청입니다."
    ),
    NICKNAME_DUPLICATED(
        "NICKNAME_DUPLICATED", "이미 사용 중인 닉네임입니다."
    ),
    USERNAME_DUPLICATED(
        "USERNAME_DUPLICATED", "이미 사용 중인 아이디입니다."
    ),

}