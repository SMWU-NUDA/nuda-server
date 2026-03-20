package smu.nuda.domain.search.controller;

import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "[SEARCH] 검색 API")
public class SearchController {

    private final SearchService searchService;
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/products/search")
    @LoginRequired
    @Operation(
            summary = "상품 검색",
            description = "Nori(상품명) 및 N-gram(성분명) 분석기를 이용해 관련도순으로 검색합니다. 검색 시 해당 키워드의 주간 인기 순위 점수가 비동기로 집계됩니다."
    )
    @SecurityRequirement(name = "JWT")
    public ApiResponse<CursorPageResponse<ProductItem>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = authenticationGuard.currentMemberId();

        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.length() < 2) {
            throw new DomainException(SearchErrorCode.KEYWORD_TOO_SHORT);
        }
        return ApiResponse.success(searchService.searchProducts(normalizedKeyword, cursor, size));
    }

    @GetMapping("/search/suggest")
    @LoginRequired
    @Operation(
            summary = "검색어 자동완성",
            description = "INGREDIENT: 성분명 최대 10개(PostgreSQL). PRODUCT: 상품명 최대 5개(ES) + 성분명 최대 5개(PostgreSQL) 병합. " +
                    "type 파라미터는 대문자(INGREDIENT, PRODUCT) 필수. 인기 검색어 집계 비대상. 사용자당 초당 5회 초과 시 429."
    )
    @SecurityRequirement(name = "JWT")
    public ApiResponse<List<String>> suggest(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "INGREDIENT") SuggestType type) {
        String trimmed = keyword.trim();
        if (trimmed.length() < 2) {
            throw new DomainException(SearchErrorCode.KEYWORD_TOO_SHORT);
        }
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(searchService.suggestKeywords(memberId, trimmed, type));
    }

    @GetMapping("/products/search/popular")
    @LoginRequired
    @Operation(
            summary = "주간 인기 검색어 Top 10",
            description = "최근 일주일간 가장 많이 검색된 키워드 상위 10개를 실시간 집계합니다."
    )
    @SecurityRequirement(name = "JWT")
    public ApiResponse<List<String>> popularKeywords() {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(searchService.getPopularKeywords());
    }
}
