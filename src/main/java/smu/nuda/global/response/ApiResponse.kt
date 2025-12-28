package smu.nuda.global.response

import smu.nuda.global.error.ErrorCode

data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val data: T? = null
) {
    companion object {

        @JvmStatic
        fun <T> success(data: T? = null): ApiResponse<T> =
            ApiResponse(
                success = true,
                code = "SUCCESS",
                message = "요청 성공",
                data = data
            )

        fun fail(errorCode: ErrorCode): ApiResponse<Nothing> =
            ApiResponse(
                success = false,
                code = errorCode.code,
                message = errorCode.message,
                data = null
            )
    }
}
