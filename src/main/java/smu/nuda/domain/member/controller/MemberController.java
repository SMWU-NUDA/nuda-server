package smu.nuda.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "[MEMBER] 회원 API")
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
    public ApiResponse<DeliveryResponse> updateDelivery(@RequestBody @Valid DeliveryRequest request) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(memberService.updateDelivery(member, request));
    }

    @PostMapping("/withdraw")
    @Operation(
            summary = "회원 탈퇴 요청",
            description = "탈퇴 요청 즉시 좋아요/장바구니가 삭제되며, 30일 이내에 취소할 수 있습니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<String> withdraw() {
        withdrawService.withdraw();
        return ApiResponse.success("회원 탈퇴 요청이 완료되었습니다.");
    }

    @DeleteMapping("/withdraw")
    @Operation(
            summary = "회원 탈퇴 취소",
            description = "탈퇴 요청 후 30일 이내에 취소할 수 있습니다. 단, 삭제된 좋아요/장바구니는 복구되지 않습니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
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
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<MyPageResponse> getMyPage() {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(memberService.getMyPage(member));
    }
}
