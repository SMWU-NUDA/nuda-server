package smu.nuda.global.error

enum class MlErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    UNAVAILABLE(
        "ML_UNAVAILABLE", "ML 서비스가 현재 사용 불가능합니다."
    ),
    TIMEOUT(
        "TIMEOUT", "ML 응답 시간이 초과되었습니다."
    )

    ;
}