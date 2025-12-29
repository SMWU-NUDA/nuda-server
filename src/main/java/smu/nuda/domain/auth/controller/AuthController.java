package smu.nuda.domain.auth.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    public ApiResponse<String> requestVerification(@RequestBody EmailVerificationRequest request) {
        authService.requestVerificationCode(request.getEmail());
        return ApiResponse.success("해당 이메일로 인증번호를 전송했습니다.");
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
    public ApiResponse<String> updateDelivery(@RequestBody DeliveryRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.updateDelivery(request, userDetails.getMember());
        return ApiResponse.success("배송지입력이 완료되었습니다. 설문조사를 진행해주세요.");
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
    public ApiResponse<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getMember());
        return ApiResponse.success("로그아웃 되었습니다.");
    }

    @GetMapping("/search/nickname")
    public ApiResponse<String> checkNickname(@RequestParam String nickname) {
        authService.checkNickname(nickname);
        return ApiResponse.success("사용 가능한 닉네임 입니다.");
    }

    @GetMapping("/search/username")
    public ApiResponse<String> checkUsername(@RequestParam String username) {
        authService.checkUsername(username);
        return ApiResponse.success("사용 가능한 아이디 입니다.");
    }
}
