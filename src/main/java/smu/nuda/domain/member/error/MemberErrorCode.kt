package smu.nuda.domain.member.error

import smu.nuda.global.error.ErrorCode

enum class MemberErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_CREDENTIALS(
        "MEMBER_INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."
    ),
    EMAIL_ALREADY_EXISTS(
        "MEMBER_EMAIL_DUPLICATED", "이미 가입된 이메일입니다."
    ),
    EMAIL_VERIFICATION_EXPIRED(
        "MEMBER_EMAIL_VERIFICATION_EXPIRED", "인증번호가 만료되었습니다."
    ),
    EMAIL_VERIFICATION_MISMATCH(
        "MEMBER_EMAIL_VERIFICATION_MISMATCH", "인증번호가 일치하지 않습니다."
    ),
    EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS(
        "MEMBER_EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS", "인증번호 입력 횟수를 초과했습니다. 인증번호를 다시 요청해주세요."
    ),
    EMAIL_NOT_VERIFIED(
        "MEMBER_EMAIL_NOT_VERIFIED", "이메일 인증이 완료되지 않았습니다."
    ),
    MEMBER_NOT_ACTIVE(
        "MEMBER_NOT_ACTIVE", "회원가입이 완료되지 않은 회원입니다."
    ),
    NICKNAME_DUPLICATED(
        "MEMBER_NICKNAME_DUPLICATED", "이미 사용 중인 닉네임입니다."
    ),
    USERNAME_DUPLICATED(
        "MEMBER_USERNAME_DUPLICATED", "이미 사용 중인 아이디입니다."
    ),
    ACCOUNT_DISABLED(
        "MEMBER_ACCOUNT_DISABLED","해당 계정은 비활성화 상태입니다."
    ),
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
        "MEMBER_SIGNUP_STEP_REQUIRED", "회원가입의 이전 단계를 먼저 완료해주세요."
    ),
    INVALID_STATUS(
        "MEMBER_INVALID_STATUS","해당 계정은 활성화 상태가 아닙니다."
    ),
    WITHDRAW_COOLDOWN(
        "MEMBER_WITHDRAW_COOLDOWN","최근 탈퇴 요청 이력이 있어 잠시 후에 다시 시도할 수 있습니다."
    )
}
