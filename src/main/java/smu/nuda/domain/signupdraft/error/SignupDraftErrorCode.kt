package smu.nuda.domain.signupdraft.error

import smu.nuda.global.error.ErrorCode

enum class SignupDraftErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {

    DRAFT_NOT_FOUND(
        "SIGNUP_DRAFT_NOT_FOUND" , "회원가입 진행 정보를 찾을 수 없습니다."
    ),
    MISSING_TOKEN(
        "SIGNUP_MISSING_TOKEN" , "회원가입 토큰이 누락되었습니다. Signup-Token 헤더를 확인해 주세요."
    ),
    DRAFT_NOT_COMPLETED(
        "SIGNUP_DRAFT_NOT_COMPLETED", "회원가입이 아직 완료되지 않았습니다."
    ),
    INVALID_KEYWORD_PRODUCT_SELECTION(
        "SIGNUP_INVALID_KEYWORD_PRODUCT_SELECTION", "유효하지 않은 설문 상품 선택입니다. 상품 정보를 다시 확인해 주세요."
    ),
    JSON_DESERIALIZATION_FAILED(
        "SIGNUP_JSON_DESERIALIZATION_FAILED", "설문 상품 JSON 데이터를 읽는 데 실패했습니다."
    ),
    JSON_SERIALIZATION_FAILED(
        "SIGNUP_JSON_SERIALIZATION_FAILED","설문 상품 데이터를 JSON으로 변환하는 데 실패했습니다."
    ),
    DUPLICATED_KEYWORD_PRODUCT_SELECTION(
        "SIGNUP_DUPLICATED_KEYWORD_PRODUCT_SELECTION","중복 선택된 설문 상품이 있습니다."
    ),
    KEYWORD_PRODUCT_NOT_FOUND(
        "SIGNUP_KEYWORD_PRODUCT_NOT_FOUND","유효하지 않은 설문 상품이 포함되어 있습니다."
    )

    ;
}
