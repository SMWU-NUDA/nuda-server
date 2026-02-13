package smu.nuda.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.ingredient.dto.IngredientSummaryResponse;
import smu.nuda.domain.ingredient.service.IngredientService;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.dto.ProductDetailResponse;
import smu.nuda.domain.product.service.ProductService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "[PRODUCT] 생리대 상품 API")
public class ProductController {

    private final ProductService productService;
    private final IngredientService ingredientService;
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/{productId}")
    @Operation(
            summary = "상품 상세 페이지 조회",
            description = "고유 번호에 해당하는 상품 상세 내용를 조회합니다. 전체 화면 중 '상품 정보'에 해당하는 부분 값만 반환합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(productService.getProductDetail(productId, member));
    }

    @GetMapping("/{productId}/ingredient-summary")
    @Operation(
            summary = "상품 성분 구성 요약",
            description = "해당 상품의 레이어별(표지, 흡수체 등) 성분 구성 요약을 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<IngredientSummaryResponse> getSummary(@PathVariable Long productId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(ingredientService.getIngredientSummary(productId, member.getId()));
    }
}
