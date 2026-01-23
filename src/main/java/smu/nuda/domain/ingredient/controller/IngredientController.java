package smu.nuda.domain.ingredient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.ingredient.dto.IngredientSummaryResponse;
import smu.nuda.domain.ingredient.service.IngredientService;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "[INGREDIENT] 상품 성분 API")
public class IngredientController {
    private final IngredientService ingredientService;
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/products/{productId}/ingredient-summary")
    @Operation(
            summary = "상품 성분 요약 정보 조회",
            description = "특정 상품의 성분 구성 및 사용자의 관심 성분을 요약하여 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<IngredientSummaryResponse> getIngredientSummary(@PathVariable Long productId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(ingredientService.getIngredientSummary(productId, member));
    }

}
