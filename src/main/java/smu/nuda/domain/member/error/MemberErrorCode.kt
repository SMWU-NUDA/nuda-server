package smu.nuda.domain.member.error

import smu.nuda.global.error.ErrorCode

enum class MemberErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    MEMBER_NOT_FOUND(
        "MEMBER_NOT_FOUND", "존재하지 않는 회원입니다"
    ),
    DUPLICATED_USERNAME(
        "MEMBER_DUPLICATED_USERNAME", "이미 사용 중인 아이디입니다"
    ),
    DUPLICATED_EMAIL(
        "MEMBER_DUPLICATED_EMAIL", "이미 사용 중인 이메일입니다"
    ),
}
