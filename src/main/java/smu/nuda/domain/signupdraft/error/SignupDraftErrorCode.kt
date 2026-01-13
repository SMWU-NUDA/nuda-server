package smu.nuda.domain.signupdraft.error

import smu.nuda.global.error.ErrorCode

enum class SignupDraftErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {

    DRAFT_NOT_FOUND(
        "SIGNUP_DRAFT_NOT_FOUND" , "회원가입 진행 정보를 찾을 수 없습니다."
    ),
    DRAFT_NOT_COMPLETED(
        "SIGNUP_DRAFT_NOT_COMPLETED", "회원가입이 아직 완료되지 않았습니다."
    ),
    INVALID_SURVEY_PRODUCT_SELECTION(
        "SIGNUP_INVALID_SURVEY_PRODUCT_SELECTION", "유효하지 않은 설문 상품 선택입니다. 상품 정보를 다시 확인해 주세요."
    ),
    MALFORMED_JSON_DATA(
        "SIGNUP_MALFORMED_JSON_DATA", "요청한 설문 상품이 유효한 JSON 포맷이 아닙니다."
    ),

    ;
}
