package smu.nuda.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.auth.dto.*;
import smu.nuda.domain.auth.service.AuthService;
import smu.nuda.domain.member.dto.MeResponse;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.annotation.ValidationBadRequestDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "[AUTH] 인증 API", description = "이메일 로그인 및 토큰 재발급 관련 API")
@CommonServerErrorDocs
public class AuthController {

    private final AuthService authService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/emails/verification-requests")
    @Operation(
            summary = "이메일 인증번호 요청",
            description = "회원가입을 위해 입력한 이메일로 인증번호를 발송합니다. 이미 가입된 이메일인 경우 요청이 거부됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "emailAlreadyExists",
                                    summary = "이미 가입된 이메일",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "MEMBER_EMAIL_DUPLICATED",
                                              "message": "이미 가입된 이메일입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ApiResponse<String> requestVerification(@RequestBody EmailVerifyRequest request) {
        authService.requestVerificationCode(request.getEmail());
        return ApiResponse.success("해당 이메일로 인증번호를 전송했습니다.");
    }

    @PostMapping("/emails/verifications")
    @Operation(
            summary = "이메일 인증번호 검증",
            description = "이메일로 발송된 인증번호를 검증합니다. 인증에 성공하면 해당 이메일은 회원가입이 가능한 상태로 변경됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "emailVerificationExpired",
                                            summary = "인증번호 만료",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_EMAIL_VERIFICATION_EXPIRED",
                                                      "message": "인증번호가 만료되었습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "emailVerificationMismatch",
                                            summary = "인증번호 불일치",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_EMAIL_VERIFICATION_MISMATCH",
                                                      "message": "인증번호가 일치하지 않습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "emailVerificationTooManyAttempts",
                                            summary = "입력 횟수 초과",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS",
                                                      "message": "인증번호 입력 횟수를 초과했습니다. 인증번호를 다시 요청해주세요.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public ApiResponse<Boolean> verifyEmailCode(@RequestBody EmailCodeVerifyRequest request) {
        return ApiResponse.success(authService.verifyEmailCode(request.getEmail(), request.getCode()));
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호를 통해 로그인합니다. 로그인에 성공하면 Access Token과 Refresh Token이 발급됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "invalidCredentials",
                                            summary = "아이디 또는 비밀번호 오류",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_INVALID_CREDENTIALS",
                                                      "message": "아이디 또는 비밀번호가 올바르지 않습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "memberWithdrawn",
                                            summary = "탈퇴한 회원",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_WITHDRAWN",
                                                      "message": "탈퇴한 회원입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "memberNotActive",
                                            summary = "비활성 회원",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_NOT_ACTIVE",
                                                      "message": "회원가입이 완료되지 않은 회원입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/reissue")
    @Operation(
            summary = "토큰 재발급",
            description = "만료된 Access Token을 대신하여 유효한 Refresh Token을 사용해 새로운 Access Token과 Refresh Token을 발급받습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "invalidRefreshToken",
                                            summary = "유효하지 않은 리프레시 토큰",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "AUTH_INVALID_REFRESH_TOKEN",
                                                      "message": "유효하지 않은 리프레시 토큰입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalidTokenType",
                                            summary = "잘못된 토큰 타입",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "AUTH_INVALID_TOKEN_TYPE",
                                                      "message": "유효하지 않은 토큰 타입입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "memberNotFound",
                                            summary = "회원 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_NOT_FOUND",
                                                      "message": "존재하지 않는 회원입니다",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public ApiResponse<ReissueResponse> reissue(@RequestBody ReissueRequest request) {
        return ApiResponse.success(authService.reissue(request.getRefreshToken()));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Access 토큰 검증",
            description = "헤더의 Access Token이 유효한지 검증합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<MeResponse> getMe(){
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(MeResponse.from(member));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "현재 로그인된 사용자의 Refresh Token을 만료시켜 로그아웃 처리합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<String> logout() {
        Long memberId = authenticationGuard.currentMemberId();
        authService.logout(memberId);
        return ApiResponse.success("로그아웃 되었습니다.");
    }

    @GetMapping("/search/nickname")
    @Operation(
            summary = "닉네임 중복 검사",
            description = "회원가입 시 사용하려는 닉네임의 중복 여부를 검사합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "nicknameDuplicated",
                                    summary = "이미 사용 중인 닉네임",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "MEMBER_NICKNAME_DUPLICATED",
                                              "message": "이미 사용 중인 닉네임입니다.",
                                              "data": "exampleNickname"
                                            }
                                            """
                            )
                    )
            )
    })
    public ApiResponse<String> checkNickname(@RequestParam String nickname) {
        authService.checkNickname(nickname);
        return ApiResponse.success("사용 가능한 닉네임 입니다.");
    }

    @GetMapping("/search/username")
    @Operation(
            summary = "아이디 중복 검사",
            description = "회원가입 시 사용하려는 아이디(username)의 중복 여부를 검사합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "usernameDuplicated",
                                    summary = "이미 사용 중인 아이디",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "MEMBER_USERNAME_DUPLICATED",
                                              "message": "이미 사용 중인 아이디입니다.",
                                              "data": "exampleUsername"
                                            }
                                            """
                            )
                    )
            )
    })
    public ApiResponse<String> checkUsername(@RequestParam String username) {
        authService.checkUsername(username);
        return ApiResponse.success("사용 가능한 아이디 입니다.");
    }

    @PostMapping("/password/verifications")
    @Operation(
            summary = "기존 비밀번호 확인",
            description = "현재 로그인한 사용자의 비밀번호와 일치하는지 확인합니다. " +
                    "회원 정보 수정시 본인 확인 용도로 사용됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "invalidPassword",
                                    summary = "비밀번호 불일치",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "AUTH_INVALID_PASSWORD",
                                              "message": "비밀번호가 일치하지 않습니다. 다시 확인해주세요.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    @ValidationBadRequestDocs
    public ApiResponse<String> verifyPassword(@Valid @RequestBody PasswordVerifyRequest request) {
        Member member = authenticationGuard.currentMember();
        authService.verifyPassword(member, request.getPassword());
        return ApiResponse.success("비밀번호 인증에 성공하였습니다.");
    }
}
