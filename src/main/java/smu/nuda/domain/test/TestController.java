package smu.nuda.domain.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.global.error.BusinessException;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/success")
    public ApiResponse<String> success() {
        return ApiResponse.success("응답코드 구조 테스트입니다.");
    }

    @GetMapping("/fail")
    public ApiResponse<Void> fail() {
        throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
    }
}
