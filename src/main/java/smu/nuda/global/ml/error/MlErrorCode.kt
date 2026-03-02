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
    )

    ;
}