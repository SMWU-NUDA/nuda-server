package smu.nuda.global.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import smu.nuda.global.error.DomainException
import smu.nuda.global.error.CommonErrorCode
import smu.nuda.global.error.ValidationErrorCode
import smu.nuda.global.error.ValidationErrorDetail
import smu.nuda.global.response.ApiResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(
        e: DomainException
    ): ResponseEntity<ApiResponse<Any>> {

        return ResponseEntity
            .badRequest()
            .body(
                ApiResponse.fail(
                    e.errorCode,
                    e.data
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        e: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        if (request.requestURI.startsWith("/v3/api-docs")) {
            throw e
        }

        log.error(
            "UNEXPECTED ERROR - method: {}, uri: {}, message: {}",
            request.method,
            request.requestURI,
            e.message,
            e
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail(CommonErrorCode.INTERNAL_SERVER_ERROR))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<ApiResponse<List<ValidationErrorDetail>>> {

        val details = e.bindingResult.fieldErrors.map { fieldError ->
            ValidationErrorDetail(
                field = fieldError.field,
                rejectedValue = fieldError.rejectedValue,
                reason = fieldError.defaultMessage ?: "유효하지 않은 값입니다"
            )
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.fail(
                    ValidationErrorCode.INVALID_PARAMETER,
                    details
                )
            )
    }

}