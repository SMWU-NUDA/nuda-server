package smu.nuda.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.like.dto.LikeToggleResponse;
import smu.nuda.domain.like.dto.LikedBrandResponse;
import smu.nuda.domain.like.service.LikeService;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brands")
@Tag(name = "[BRAND LIKE] 생리대 브랜드 찜하기 API")
public class BrandLikeController {

    private  final LikeService likeService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/{brandId}/likes")
    @Operation(
            summary = "브랜드 찜하기",
            description = "로그인한 사용자가 고유번호에 해당하는 브랜드를 찜하거나 찜하거나 취소합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<LikeToggleResponse> toggle(@PathVariable Long brandId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(likeService.brandLikeToggle(brandId, member));
    }

    @GetMapping("/likes")
    @Operation(
            summary = "사용자의 찜한 브랜드 조회",
            description = "로그인한 사용자가 찜한 전체 브랜드 목록을 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CursorPageResponse<LikedBrandResponse>> products(
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(likeService.likedBrands(member, cursor, size));
    }

}
