package smu.nuda.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.member.dto.DeliveryRequest;
import smu.nuda.domain.member.dto.DeliveryResponse;
import smu.nuda.domain.member.dto.MeResponse;
import smu.nuda.domain.member.dto.UpdateMemberRequest;
import smu.nuda.domain.member.service.MemberService;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "[MEMBER] 회원 API")
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/me")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "회원 정보 수정",
            description = "닉네임, 아이디, 이메일, 비밀번호를 선택적으로 수정합니다."
    )
    public ApiResponse<MeResponse> updateMe(@RequestBody UpdateMemberRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberService.updateMe(userDetails.getMember(), request));
    }

    @GetMapping("/members/me/delivery")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "배송정보 조회",
            description = "수령인, 전화번호, 주소지를 조회합니다."
    )
    public ApiResponse<DeliveryResponse> getDelivery(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberService.getDelivery(userDetails.getMember()));
    }


    @PutMapping("/members/me/delivery")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "배송정보 수정",
            description = "수령인, 전화번호, 주소지를 전체적으로 수정합니다."
    )
    public ApiResponse<DeliveryResponse> updateDelivery(@RequestBody @Valid DeliveryRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberService.updateDelivery(userDetails.getMember(), request));
    }

}
