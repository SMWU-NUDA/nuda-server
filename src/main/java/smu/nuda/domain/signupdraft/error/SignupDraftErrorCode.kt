package smu.nuda.domain.signupdraft.error

import smu.nuda.global.error.ErrorCode

enum class SignupDraftErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {

    DRAFT_NOT_FOUND(
        "SIGNUP_DRAFT_NOT_FOUND" , "회원가입 진행 정보를 찾을 수 없습니다."
    ),
    INVALID_SURVEY_FORMAT(
        "INVALID_SURVEY_FORMAT", "설문 데이터 형식이 올바르지 않습니다."
    )

    ;
}
