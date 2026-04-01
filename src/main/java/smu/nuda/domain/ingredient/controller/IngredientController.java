package smu.nuda.domain.ingredient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "[INGREDIENT] 성분 API")
@CommonServerErrorDocs
public class IngredientController {

    private final IngredientService ingredientService;
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/ingredients/search/popular")
    @Operation(
            summary = "성분 주간 인기 검색어 Top 10",
            description = "최근 일주일간 가장 많이 검색된 성분 키워드 상위 10개를 실시간 집계합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<List<String>> popularIngredientKeywords() {
        return ApiResponse.success(ingredientService.getPopularKeywords());
    }

    @GetMapping("/ingredients/search")
    @Operation(
            summary = "성분 검색",
            description = "성분명 키워드로 성분을 검색합니다. 대소문자를 구분하지 않으며, 키워드로 시작하는 성분이 우선 표시됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "keywordTooShort",
                                    summary = "검색어 길이 부족",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INGREDIENT_KEYWORD_TOO_SHORT",
                                              "message": "검색어는 2글자 이상 입력해주세요.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<List<IngredientItem>> searchIngredients(@RequestParam String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.length() < 2) {
            throw new DomainException(IngredientErrorCode.KEYWORD_TOO_SHORT);
        }
        return ApiResponse.success(ingredientService.search(normalizedKeyword));
    }

    @GetMapping("/products/{productId}/ingredient-summary")
    @Operation(
            summary = "상품 성분 구성 요약",
            description = "해당 상품의 레이어별(표지, 흡수체 등) 성분 구성 요약을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "invalidProduct",
                                    summary = "유효하지 않은 상품",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "PRODUCT_INVALID",
                                              "message": "유효하지 않은 상품입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<IngredientSummaryResponse> getSummary(@PathVariable Long productId) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(ingredientService.getIngredientSummary(productId, memberId));
    }

    @GetMapping("/products/{productId}/ingredients")
    @Operation(
            summary = "상품 전성분 조회",
            description = "해당 상품의 전체 성분을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "invalidProduct",
                                            summary = "유효하지 않은 상품",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PRODUCT_INVALID",
                                                      "message": "유효하지 않은 상품입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "emptyProductContent",
                                            summary = "성분 정보 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "EMPTY_PRODUCT_CONTENT",
                                                      "message": "상품에 등록된 성분 정보가 존재하지 않습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
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
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "invalidIngredient",
                                    summary = "유효하지 않은 성분",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_INGREDIENT",
                                              "message": "유효하지 않은 성분 정보입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<IngredientDetailResponse> getIngredientDetail(@PathVariable Long ingredientId) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(ingredientService.getIngredientDetail(ingredientId, memberId));
    }
}
