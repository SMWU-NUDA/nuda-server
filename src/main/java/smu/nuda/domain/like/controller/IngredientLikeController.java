package smu.nuda.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
}
