package smu.nuda.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "[AUTH] 인증 API", description = "이메일 로그인 및 토큰 재발급 관련 API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/emails/verification-requests")
    @Operation(
            summary = "이메일 인증번호 요청",
            description = "회원가입을 위해 입력한 이메일로 인증번호를 발송합니다. 이미 가입된 이메일인 경우 요청이 거부됩니다."
    )
    public ApiResponse<String> requestVerification(@RequestBody EmailVerificationRequest request) {
        authService.requestVerificationCode(request.getEmail());
        return ApiResponse.success("해당 이메일로 인증번호를 전송했습니다.");
    }

    @PostMapping("/emails/verifications")
    @Operation(
            summary = "이메일 인증번호 검증",
            description = "이메일로 발송된 인증번호를 검증합니다. 인증에 성공하면 해당 이메일은 회원가입이 가능한 상태로 변경됩니다."
    )
    public ApiResponse<Boolean> verifyEmailCode(@RequestBody EmailCodeVerifyRequest request) {
        return ApiResponse.success(authService.verifyEmailCode(request.getEmail(), request.getCode()));
    }

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입 정보 입력(1단계)",
            description = "이메일 인증이 완료된 사용자가 아이디, 비밀번호, 닉네임 등 기본 회원 정보를 입력하여 회원가입을 진행합니다."
    )
    public ApiResponse<SignupResponse> signup(@RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signup(request));
    }

    @PatchMapping("/delivery")
    @Operation(
            summary = "배송지 정보 입력(2단계)",
            description = "회원가입 과정 중 배송지 정보를 입력합니다. 회원가입 정보 입력 후 요청해주세요."
    )
    @SecurityRequirement(name = "JWT")
    public ApiResponse<String> updateDelivery(@RequestBody DeliveryRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.updateDelivery(request, userDetails.getMember());
        return ApiResponse.success("배송지입력이 완료되었습니다. 설문조사를 진행해주세요.");
    }

    @PostMapping("/complete")
    @Operation(
            summary = "회원가입 완료(4단계)",
            description = "배송지 입력까지 완료된 사용자의 회원가입 상태를 최종 완료 처리합니다."
    )
    public ApiResponse<String> completeSignup(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.completeSignup(userDetails.getMember());
        return ApiResponse.success("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호를 통해 로그인합니다. 로그인에 성공하면 Access Token과 Refresh Token이 발급됩니다."
    )
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/reissue")
    @Operation(
            summary = "토큰 재발급",
            description = "만료된 Access Token을 대신하여 유효한 Refresh Token을 사용해 새로운 Access Token과 Refresh Token을 발급받습니다."
    )
    public ApiResponse<ReissueResponse> reissue(@RequestBody ReissueRequest request) {
        return ApiResponse.success(authService.reissue(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "현재 로그인된 사용자의 Refresh Token을 만료시켜 로그아웃 처리합니다."
    )
    @SecurityRequirement(name = "JWT")
    public ApiResponse<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getMember());
        return ApiResponse.success("로그아웃 되었습니다.");
    }

    @GetMapping("/search/nickname")
    @Operation(
            summary = "닉네임 중복 검사",
            description = "회원가입 시 사용하려는 닉네임의 중복 여부를 검사합니다."
    )
    public ApiResponse<String> checkNickname(@RequestParam String nickname) {
        authService.checkNickname(nickname);
        return ApiResponse.success("사용 가능한 닉네임 입니다.");
    }

    @GetMapping("/search/username")
    @Operation(
            summary = "아이디 중복 검사",
            description = "회원가입 시 사용하려는 아이디(username)의 중복 여부를 검사합니다."
    )
    public ApiResponse<String> checkUsername(@RequestParam String username) {
        authService.checkUsername(username);
        return ApiResponse.success("사용 가능한 아이디 입니다.");
    }
}
