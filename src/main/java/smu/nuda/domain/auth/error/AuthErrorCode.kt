package smu.nuda.domain.auth.error

import smu.nuda.global.error.ErrorCode

enum class AuthErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_ACCESS_TOKEN(
        "AUTH_INVALID_ACCESS_TOKEN", "유효하지 않은 액세스 토큰입니다."
    ),
    EXPIRED_TOKEN(
        "AUTH_EXPIRED_TOKEN", "해당 토큰이 만료되었습니다."
    ),
    INVALID_REFRESH_TOKEN(
        "AUTH_INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."
    ),
    INVALID_TOKEN_TYPE(
        "AUTH_INVALID_TOKEN_TYPE","유효하지 않은 토큰 타입입니다."
    ),
    JWT_CONFIGURATION_NOT_FOUND(
        "AUTH_JWT_CONFIGURATION_NOT_FOUND", "JWT 토큰 설정이 존재하지 않습니다"
    ),
    AUTH_REQUIRED(
        "AUTH_REQUIRED", "로그인이 필요한 기능입니다."
    ),
    ACCESS_DENIED(
        "AUTH_ACCESS_DENIED","해당 계정은 요청 권한이 없습니다."
    ),
    INVALID_PASSWORD(
        "AUTH_INVALID_PASSWORD", "비밀번호가 일치하지 않습니다. 다시 확인해주세요."
    ),
    ;
}