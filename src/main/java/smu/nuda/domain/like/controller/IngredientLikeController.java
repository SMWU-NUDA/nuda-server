package smu.nuda.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.like.dto.LikedIngredientResponse;
import smu.nuda.domain.like.dto.PreferToggleResponse;
import smu.nuda.domain.like.service.LikeService;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
@Tag(name = "[INGREDIENTS LIKE] 성분 찜하기 API")
public class IngredientLikeController {

    private  final LikeService likeService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/{ingredientId}/likes")
    @Operation(
            summary = "성분 즐겨찾기(관심/피하기)",
            description = "로그인한 사용자가 특정 성분에 대해 관심 또는 피하기 상태를 등록하거나 취소합니다.\n" +
                    "이미 등록된 상태와 동일한 요청을 보낼 경우 해당 설정이 **취소(삭제)**됩니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<PreferToggleResponse> toggle(
            @PathVariable Long ingredientId,
            @Parameter(description = "관심 성분 등록 여부 (true: 관심, false: 피하기)") @RequestParam(defaultValue = "true") boolean preference
    ) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(likeService.ingredientPreferToggle(ingredientId, preference, member));
    }

    @GetMapping("/likes")
    @Operation(
            summary = "사용자의 성분 즐겨찾기 조회",
            description = "로그인한 사용자의 성분 즐겨찾기 목록을 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CursorPageResponse<LikedIngredientResponse>> products(
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "관심 성분 등록 여부 (true: 관심, false: 피하기)") @RequestParam(defaultValue = "true") boolean preference
    ) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(likeService.likedIngredients(member, preference, cursor, size));
    }
}
