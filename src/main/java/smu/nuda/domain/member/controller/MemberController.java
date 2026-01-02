package smu.nuda.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.member.dto.DeliveryRequest;
import smu.nuda.domain.member.dto.DeliveryResponse;
import smu.nuda.domain.member.dto.MeResponse;
import smu.nuda.domain.member.dto.UpdateMemberRequest;
import smu.nuda.domain.member.service.MemberService;
import smu.nuda.domain.member.service.WithdrawService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "[MEMBER] 회원 API")
public class MemberController {

    private final MemberService memberService;
    private final WithdrawService withdrawService;

    @PatchMapping("/me")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "회원 정보 수정",
            description = "닉네임, 아이디, 이메일, 비밀번호를 선택적으로 수정합니다."
    )
    @LoginRequired
    public ApiResponse<MeResponse> updateMe(@RequestBody UpdateMemberRequest request) {
        return ApiResponse.success(memberService.updateMe(request));
    }

    @GetMapping("/me/delivery")
    @Operation(
            summary = "배송정보 조회",
            description = "수령인, 전화번호, 주소지를 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<DeliveryResponse> getDelivery() {
        return ApiResponse.success(memberService.getDelivery());
    }

    @PutMapping("/me/delivery")
    @Operation(
            summary = "배송정보 수정",
            description = "수령인, 전화번호, 주소지를 전체적으로 수정합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<DeliveryResponse> updateDelivery(@RequestBody @Valid DeliveryRequest request) {
        return ApiResponse.success(memberService.updateDelivery(request));
    }

    @PostMapping("/withdraw")
    @Operation(
            summary = "회원 탈퇴 요청",
            description = "탈퇴 가능 여부 판단 후 관리자에게 요청이 전달됩니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<String> withdraw() {
        withdrawService.withdraw();
        return ApiResponse.success("회원 탈퇴 요청이 완료되었습니다.");
    }

}
