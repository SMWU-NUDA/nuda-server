package smu.nuda.domain.ingredient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.ingredient.dto.IngredientDetailResponse;
import smu.nuda.domain.ingredient.service.IngredientService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
@Tag(name = "[INGREDIENT] 성분 API")
public class IngredientController {

    private final IngredientService ingredientService;
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/{ingredientId}")
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
