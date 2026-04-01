package smu.nuda.domain.member.controller;

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
import smu.nuda.domain.member.dto.*;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.service.MemberService;
import smu.nuda.domain.member.service.WithdrawService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.annotation.ValidationBadRequestDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "[MEMBER] 회원 API")
@CommonServerErrorDocs
public class MemberController {

    private final MemberService memberService;
    private final WithdrawService withdrawService;
    private final AuthenticationGuard authenticationGuard;

    @PatchMapping("/me")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "회원 정보 수정",
            description = "닉네임, 아이디, 이메일, 비밀번호를 선택적으로 수정합니다."
    )
    @LoginRequired
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "emailNotVerified",
                                            summary = "이메일 인증 미완료",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_EMAIL_NOT_VERIFIED",
                                                      "message": "이메일 인증이 완료되지 않았습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "passwordRequired",
                                            summary = "현재 비밀번호 누락",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_PASSWORD_REQUIRED",
                                                      "message": "현재 비밀번호를 입력해주세요.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalidPassword",
                                            summary = "현재 비밀번호 불일치",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_INVALID_PASSWORD",
                                                      "message": "비밀번호가 올바르지 않습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @AuthUnauthorizedErrorDocs
    public ApiResponse<MeResponse> updateMe(@RequestBody UpdateMemberRequest request) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(memberService.updateMe(member, request));
    }

    @GetMapping("/me/delivery")
    @Operation(
            summary = "배송정보 조회",
            description = "수령인, 전화번호, 주소지를 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<DeliveryResponse> getDelivery() {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(memberService.getDelivery(member));
    }

    @PutMapping("/me/delivery")
    @Operation(
            summary = "배송정보 수정",
            description = "수령인, 전화번호, 주소지를 전체적으로 수정합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    @ValidationBadRequestDocs
    public ApiResponse<DeliveryResponse> updateDelivery(@RequestBody @Valid DeliveryRequest request) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(memberService.updateDelivery(member, request));
    }

    @PostMapping("/withdraw")
    @Operation(
            summary = "회원 탈퇴 요청",
            description = "탈퇴 요청 즉시 좋아요/장바구니가 삭제되며, 30일 이내에 취소할 수 있습니다."
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
                                            name = "invalidStatus",
                                            summary = "탈퇴 가능한 상태 아님",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_INVALID_STATUS",
                                                      "message": "해당 계정은 활성화 상태가 아닙니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "withdrawCooldown",
                                            summary = "탈퇴 재요청 제한",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_WITHDRAW_COOLDOWN",
                                                      "message": "최근 탈퇴 요청 이력이 있어 잠시 후에 다시 시도할 수 있습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<String> withdraw() {
        withdrawService.withdraw();
        return ApiResponse.success("회원 탈퇴 요청이 완료되었습니다.");
    }

    @DeleteMapping("/withdraw")
    @Operation(
            summary = "회원 탈퇴 취소",
            description = "탈퇴 요청 후 30일 이내에 취소할 수 있습니다. 단, 삭제된 좋아요/장바구니는 복구되지 않습니다."
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
                                            name = "notInWithdrawRequested",
                                            summary = "탈퇴 요청 상태 아님",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_NOT_IN_WITHDRAW_REQUESTED",
                                                      "message": "탈퇴 요청 상태가 아닙니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "cancellationWindowExpired",
                                            summary = "취소 가능 기간 만료",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "MEMBER_CANCELLATION_WINDOW_EXPIRED",
                                                      "message": "탈퇴 취소 가능 기간이 지났습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<String> cancelWithdraw() {
        withdrawService.cancelWithdraw();
        return ApiResponse.success("회원 탈퇴가 취소되었습니다.");
    }

    @GetMapping("/me")
    @Operation(
            summary = "마이페이지 조회",
            description = "현재 로그인한 사용자의 회원 정보(프로필 정보, 키워드)를 조회합니다." +
                    "내 리뷰는 나의 리뷰 조회 api를 요청해주세요."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "keywordNotFound",
                                    summary = "키워드 정보 없음",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "KEYWORD_NOT_FOUND",
                                              "message": "사용자의 키워드 정보를 찾을 수 없습니다.",
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
    public ApiResponse<MyPageResponse> getMyPage() {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(memberService.getMyPage(member));
    }
}
