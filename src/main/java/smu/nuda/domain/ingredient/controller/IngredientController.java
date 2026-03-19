package smu.nuda.domain.ingredient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.ingredient.dto.IngredientDetailResponse;
import smu.nuda.domain.ingredient.dto.IngredientItem;
import smu.nuda.domain.ingredient.dto.IngredientResponse;
import smu.nuda.domain.ingredient.dto.IngredientSummaryResponse;
import smu.nuda.domain.ingredient.dto.enums.IngredientFilterType;
import smu.nuda.domain.ingredient.error.IngredientErrorCode;
import smu.nuda.domain.ingredient.service.IngredientService;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "[INGREDIENT] 성분 API")
public class IngredientController {

    private final IngredientService ingredientService;
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/ingredients/search")
    @Operation(
            summary = "성분 검색",
            description = "성분명 키워드로 성분을 검색합니다. 대소문자를 구분하지 않으며, 키워드로 시작하는 성분이 우선 표시됩니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<List<IngredientItem>> searchIngredients(@RequestParam String keyword) {
        String trimmed = keyword.trim();
        if (trimmed.length() < 2) {
            throw new DomainException(IngredientErrorCode.KEYWORD_TOO_SHORT);
        }
        return ApiResponse.success(ingredientService.search(trimmed));
    }

    @GetMapping("/products/{productId}/ingredient-summary")
    @Operation(
            summary = "상품 성분 구성 요약",
            description = "해당 상품의 레이어별(표지, 흡수체 등) 성분 구성 요약을 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<IngredientSummaryResponse> getSummary(@PathVariable Long productId) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(ingredientService.getIngredientSummary(productId, memberId));
    }

    @GetMapping("/products/{productId}/ingredients")
    @Operation(
            summary = "상품 전성분 조회",
            description = "해당 상품의 전체 성분을 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<IngredientResponse> getProductIngredients(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "ALL") IngredientFilterType filter
    ) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(ingredientService.getIngredients(productId, filter, memberId));
    }

    @GetMapping("/ingredients/{ingredientId}")
    @Operation(
            summary = "성분 상세 페이지 조회",
            description = "고유 번호에 해당하는 성분 상세 내용을 조회합니다. " +
                    "preference이 true면 관심, False면 피하기, null이라면 즐겨찾기에 등록되지 않은 상태입니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<IngredientDetailResponse> getIngredientDetail(@PathVariable Long ingredientId) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(ingredientService.getIngredientDetail(ingredientId, memberId));
    }
}
