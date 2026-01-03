package smu.nuda.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import smu.nuda.global.error.ErrorCode;
import smu.nuda.global.response.ApiResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityErrorResponse {

    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> body = ApiResponse.fail(errorCode);

        response.getWriter().write(
                objectMapper.writeValueAsString(body)
        );
    }
}
