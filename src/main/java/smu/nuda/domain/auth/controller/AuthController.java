package smu.nuda.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.auth.dto.EmailCodeVerifyRequest;
import smu.nuda.domain.auth.dto.EmailVerificationRequest;
import smu.nuda.domain.auth.service.AuthService;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/verification-requests")
    public ApiResponse<Boolean> requestVerification(@RequestBody EmailVerificationRequest request) {
        return ApiResponse.success(authService.requestVerificationCode(request.getEmail()));
    }

    @PostMapping("/verifications")
    public ApiResponse<Boolean> verifyEmailCode(@RequestBody EmailCodeVerifyRequest request) {
        return ApiResponse.success(authService.verifyEmailCode(request.getEmail(), request.getCode()));
    }
}
