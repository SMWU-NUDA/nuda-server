package smu.nuda.domain.member.error

import smu.nuda.global.error.ErrorCode

enum class MemberErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    MEMBER_NOT_FOUND(
        "MEMBER_NOT_FOUND", "존재하지 않는 회원입니다"
    ),
    INVALID_PASSWORD(
        "INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."
    ),
    PASSWORD_REQUIRED(
        "PASSWORD_REQUIRED", "현재 비밀번호를 입력해주세요."
    ),
}
