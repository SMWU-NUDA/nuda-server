package smu.nuda.global.error

enum class CommonErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    INTERNAL_SERVER_ERROR(
        "COMMON_INTERNAL_ERROR", "서버 내부 오류가 발생했습니다"
    ),

    INVALID_REQUEST(
        "COMMON_INVALID_REQUEST", "잘못된 요청입니다"
    )
}