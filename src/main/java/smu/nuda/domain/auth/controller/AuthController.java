package smu.nuda.domain.auth.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.auth.dto.*;
import smu.nuda.domain.auth.service.AuthService;
import smu.nuda.domain.member.dto.DeliveryRequest;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/emails/verification-requests")
    public ApiResponse<Boolean> requestVerification(@RequestBody EmailVerificationRequest request) {
        return ApiResponse.success(authService.requestVerificationCode(request.getEmail()));
    }

    @PostMapping("/emails/verifications")
    public ApiResponse<Boolean> verifyEmailCode(@RequestBody EmailCodeVerifyRequest request) {
        return ApiResponse.success(authService.verifyEmailCode(request.getEmail(), request.getCode()));
    }

    @PostMapping("/signup")
    public ApiResponse<SignupResponse> signup(@RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signup(request));
    }

    @PatchMapping("/delivery")
    @SecurityRequirement(name = "JWT")
    public ApiResponse<Boolean> updateDelivery(@RequestBody DeliveryRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(authService.updateDelivery(request, userDetails.getMember()));
    }

    @PostMapping("/complete")
    public ApiResponse<String> completeSignup(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.completeSignup(userDetails.getMember());
        return ApiResponse.success("회원가입이 완료되었습니다.");
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
        // Todo. @AuthenticationPrincipal로 수정
        return ApiResponse.success(authService.logout(request));
    }

}
