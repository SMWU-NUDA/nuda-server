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
        @JvmOverloads
        fun <T> success(data: T? = null): ApiResponse<T> =
            ApiResponse(
                success = true,
                code = "SUCCESS",
                message = "요청 성공",
                data = data
            )

        @JvmStatic
        fun <T> success(
            data: T?,
            message: String
        ): ApiResponse<T> =
            ApiResponse(
                success = true,
                code = "SUCCESS",
                message = message,
                data = data
            )

        @JvmStatic
        @JvmOverloads
        fun <T> fail(errorCode: ErrorCode, data: T? = null): ApiResponse<T> =
            ApiResponse(
                success = false,
                code = errorCode.code,
                message = errorCode.message,
                data = data
            )
    }
}
