package smu.nuda.domain.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.product.dto.ProductItem;
import smu.nuda.domain.search.dto.enums.SuggestType;
import smu.nuda.domain.search.error.SearchErrorCode;
import smu.nuda.domain.search.service.SearchService;
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
@Tag(name = "[SEARCH] 검색 API")
@CommonServerErrorDocs
public class SearchController {

    private final SearchService searchService;
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/products/search")
    @LoginRequired
    @Operation(
            summary = "상품 검색",
            description = "Nori(상품명) 및 N-gram(성분명) 분석기를 이용해 관련도순으로 검색합니다. 검색 시 해당 키워드의 주간 인기 순위 점수가 비동기로 집계됩니다."
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
                                              "code": "SEARCH_KEYWORD_TOO_SHORT",
                                              "message": "검색어는 2글자 이상 입력해주세요.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @AuthUnauthorizedErrorDocs
    public ApiResponse<CursorPageResponse<ProductItem>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.length() < 2) {
            throw new DomainException(SearchErrorCode.KEYWORD_TOO_SHORT);
        }
        return ApiResponse.success(searchService.searchProducts(normalizedKeyword, cursor, size));
    }

    @GetMapping("/search/suggest")
    @Operation(
            summary = "검색어 자동완성",
            description = """
            type 별 동작
            - INGREDIENT: 성분명 최대 10개 반환
            - PRODUCT: 상품명 최대 5개와 성분명 최대 5개 반환
            
            트래픽 제한: 사용자당 초당 5회 초과 요청 시 400 Too Many Requests 반환
            """
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
                                            name = "keywordTooShort",
                                            summary = "검색어 길이 부족",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "SEARCH_KEYWORD_TOO_SHORT",
                                                      "message": "검색어는 2글자 이상 입력해주세요.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "suggestRateLimitExceeded",
                                            summary = "자동완성 요청 속도 제한 초과",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "SEARCH_SUGGEST_RATE_LIMIT_EXCEEDED",
                                                      "message": "요청이 너무 빠릅니다. 잠시 후 다시 시도해주세요.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public ApiResponse<List<String>> suggest(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "INGREDIENT") SuggestType type) {
        String trimmed = keyword.trim();
        if (trimmed.length() < 2) {
            throw new DomainException(SearchErrorCode.KEYWORD_TOO_SHORT);
        }
        Long memberId = authenticationGuard.currentMemberIdOrNull();
        return ApiResponse.success(searchService.suggestKeywords(memberId, trimmed, type));
    }

    @GetMapping("/products/search/popular")
    @LoginRequired
    @Operation(
            summary = "상품 주간 인기 검색어 Top 10",
            description = "최근 일주일간 가장 많이 검색된 상품 키워드 상위 10개를 실시간 집계합니다."
    )
    @SecurityRequirement(name = "JWT")
    @AuthUnauthorizedErrorDocs
    public ApiResponse<List<String>> popularKeywords() {
        return ApiResponse.success(searchService.getPopularKeywords());
    }
}
