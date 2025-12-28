package smu.nuda.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import smu.nuda.global.error.BusinessException
import smu.nuda.global.error.CommonErrorCode
import smu.nuda.global.response.ApiResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        e: BusinessException
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.fail(e.errorCode))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        e: Exception
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail(CommonErrorCode.INTERNAL_SERVER_ERROR))
    }
}