package smu.nuda.global.error

enum class MlErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    UNAVAILABLE(
        "ML_UNAVAILABLE", "ML 서비스가 현재 사용 불가능합니다."
    ),
    TIMEOUT(
        "ML_TIMEOUT", "ML 응답 시간이 초과되었습니다."
    ),
    INTERNAL_SERVER_ERROR(
        "ML_INTERNAL_SERVER_ERROR", "ML 서버가 응답하지 않습니다."
    ),
    CLIENT_ERROR(
        "ML_CLIENT_ERROR", "ML 서버 요청 형식이 잘못되었습니다."
    ),
    REVIEW_INSUFFICIENT(
        "ML_REVIEW_INSUFFICIENT", "리뷰가 충분하지 않아 AI 분석을 수행할 수 없습니다."
    ),
    EMPTY_RESPONSE(
        "ML_EMPTY_RESPONSE","ML 서버가 빈 응답을 반환했습니다."
    ),

    ;
}