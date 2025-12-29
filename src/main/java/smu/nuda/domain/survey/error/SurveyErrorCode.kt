package smu.nuda.domain.survey.error

import smu.nuda.global.error.ErrorCode

enum class SurveyErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    SURVEY_ALREADY_EXISTS(
        "SURVEY_ALREADY_EXISTS", "이미 설문을 제출한 회원입니다"
    ),
    SURVEY_MEMBER_NOT_FOUND(
        "SURVEY_MEMBER_NOT_FOUND", "설문을 제출할 회원을 찾을 수 없습니다"
    ),
    SURVEY_NOT_FOUND(
        "SURVEY_NOT_FOUND", "설문 정보를 찾을 수 없습니다"
    ),
    SURVEY_PRODUCT_DUPLICATED(
        "SURVEY_PRODUCT_DUPLICATED", "이미 선택된 설문 상품이 포함되어 있습니다"
    ),
}
