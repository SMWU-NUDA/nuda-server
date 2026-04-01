package smu.nuda.global.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import smu.nuda.global.error.ErrorCode
import smu.nuda.global.response.ApiResponse

@Component
class SecurityErrorResponse(
    private val objectMapper: ObjectMapper
) {

    fun write(response: HttpServletResponse, errorCode: ErrorCode) {
        response.contentType = "application/json;charset=UTF-8"

        val body: ApiResponse<Nothing> = ApiResponse.fail(errorCode)
        response.writer.write(objectMapper.writeValueAsString(body))
    }
}
