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
import smu.nuda.domain.search.error.SearchErrorCode;
import smu.nuda.domain.search.service.ProductSearchService;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Tag(name = "[SEARCH] 검색 API")
public class SearchController {

    private final ProductSearchService productSearchService;

    @GetMapping("/products")
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
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.length() < 2) {
            throw new DomainException(SearchErrorCode.KEYWORD_TOO_SHORT);
        }
        return ApiResponse.success(productSearchService.search(normalizedKeyword, cursor, size));
    }

    @GetMapping("/keywords/popular")
    @LoginRequired
    @Operation(
            summary = "주간 인기 검색어 Top 10",
            description = "최근 일주일간 가장 많이 검색된 키워드 상위 10개를 실시간 집계합니다."
    )
    @SecurityRequirement(name = "JWT")
    public ApiResponse<List<String>> popularKeywords() {
        return ApiResponse.success(productSearchService.getPopularKeywords());
    }
}
