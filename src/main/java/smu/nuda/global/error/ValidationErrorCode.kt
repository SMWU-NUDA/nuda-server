package smu.nuda.global.error

enum class ValidationErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_PARAMETER(
        "VALIDATION_INVALID_PARAMETER", "요청 값이 올바르지 않습니다"
    )
}