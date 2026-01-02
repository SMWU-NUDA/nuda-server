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
        "MEMBER_INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."
    ),
    PASSWORD_REQUIRED(
        "MEMBER_PASSWORD_REQUIRED", "현재 비밀번호를 입력해주세요."
    ),
    SIGNUP_NOT_COMPLETED(
        "SIGNUP_NOT_COMPLETED", "회원가입이 완료되지 않았습니다."
    ),

    SIGNUP_STEP_REQUIRED(
        "SIGNUP_STEP_REQUIRED", "회원가입의 이전 단계를 먼저 완료해주세요."
    ),
    INVALID_STATUS(
        "MEMBER_INVALID_STATUS","해당 계정은 활성화 상태가 아닙니다."
    ),
    WITHDRAW_COOLDOWN(
        "MEMBER_WITHDRAW_COOLDOWN","최근 탈퇴 요청 이력이 있어 잠시 후에 다시 시도할 수 있습니다."
    )
}
