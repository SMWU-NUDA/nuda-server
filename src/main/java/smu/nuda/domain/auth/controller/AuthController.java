package smu.nuda.domain.auth.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.auth.dto.*;
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

    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ApiResponse.success("member 엔티티 생성 완료. 나머지 과정을 진행해주세요.");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/reissue")
    public ApiResponse<ReissueResponse> reissue(@RequestBody ReissueRequest request) {
        return ApiResponse.success(authService.reissue(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "JWT")
    public ApiResponse<Boolean> logout(HttpServletRequest request) {
        return ApiResponse.success(authService.logout(request));
    }

}
