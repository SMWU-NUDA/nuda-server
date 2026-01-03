package smu.nuda.domain.test;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.response.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/success")
    public ApiResponse<String> success() {
        return ApiResponse.success("응답코드 구조 테스트입니다.");
    }

    @GetMapping("/fail")
    public ApiResponse<Void> fail() {
        throw new DomainException(MemberErrorCode.INVALID_CREDENTIALS);
    }

    @GetMapping("/fail/with-data")
    public ApiResponse<Void> failWithData() {
        throw new DomainException(
                MemberErrorCode.EMAIL_ALREADY_EXISTS,
                Map.of(
                        "email", "test@example.com",
                        "reason", "이미 가입된 이메일"
                )
        );
    }

    @PostMapping("/validation")
    public ApiResponse<String> testValidation(@Valid @RequestBody TestValidationRequest request) {
        return ApiResponse.success("Validation 통과");
    }
}
