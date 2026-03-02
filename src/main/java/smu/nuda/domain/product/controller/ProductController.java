package smu.nuda.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.common.dto.Cursor;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.common.dto.CursorResponse;
import smu.nuda.domain.product.dto.ProductDetailResponse;
import smu.nuda.domain.product.dto.ProductItem;
import smu.nuda.domain.product.dto.enums.ProductKeywordType;
import smu.nuda.domain.product.dto.enums.ProductSortType;
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
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/{productId}")
    @Operation(
            summary = "상품 상세 페이지 조회",
            description = "고유 번호에 해당하는 상품 상세 내용를 조회합니다. 전체 화면 중 '상품 정보'에 해당하는 부분 값만 반환합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(productService.getProductDetail(productId, memberId));
    }

    @GetMapping
    @Operation(
            summary = "전체 상품 랭킹 조회",
            description = """
            정렬 기준에 따라 전체 상품을 커서 기반 무한 스크롤 방식으로 조회합니다.
            - DEFAULT : 기본순 (최신 등록 순)
            - REVIEW_COUNT_DESC : 리뷰 많은 순
            - RATING_DESC : 별점 높은 순
            - RATING_ASC : 별점 낮은 순
            - LIKE_COUNT_DESC : 찜 많은 순
        
            cursor 파라미터는 "{sortValue}_{id}" 형식입니다.
            eg. 4.5_123
            """
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CursorResponse<ProductItem, Number>> getProducts(
            @RequestParam(defaultValue = "DEFAULT") ProductSortType sort,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        Cursor<Number> parsedCursor = parseCursor(cursor, sort);
        return ApiResponse.success(productService.getSortedProducts(sort, parsedCursor, size));
    }

    private Cursor<Number> parseCursor(String cursorStr, ProductSortType sortType) {
        if (cursorStr == null || cursorStr.isBlank()) return null;

        String[] parts = cursorStr.split("_");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid cursor format");
        }

        String sortValueStr = parts[0];
        Long id = Long.parseLong(parts[1]);
        Number sortValue = switch (sortType) {
            case REVIEW_COUNT_DESC, LIKE_COUNT_DESC ->
                    Integer.parseInt(sortValueStr);
            case RATING_DESC, RATING_ASC ->
                    Double.parseDouble(sortValueStr);
            case DEFAULT ->
                    id;
        };

        return new Cursor<>(sortValue, id);
    }

    @GetMapping("/global-rankings")
    @Operation(
            summary = "키워드별 전체 상품 랭킹 조회",
            description = """
                    키워드(자극도, 향, 흡수력 등)를 기준으로 정렬된 전체 상품의 랭킹을 조회합니다.
                    - DEFAULT : 전체
                    - IRRITATION_LEVEL : 민감도 순
                    - SCENT : 향 순
                    - ABSORBENCY : 흡수력 순
                    - ADHESION : 접착력 순
                    """
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CursorPageResponse<ProductItem>> getGlobalRanking(
            @RequestParam ProductKeywordType keyword,
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(productService.getGlobalRankingPage(keyword, cursor, size));
    }

    @GetMapping("/personal-rankings")
    @Operation(
            summary = "키워드별 맞춤 상품 랭킹 조회",
            description = """
                    키워드(자극도, 향, 흡수력 등)를 기준으로 정렬된 사용자 맞춤 랭킹 상품을 조회합니다.
                    - DEFAULT : 전체
                    - IRRITATION_LEVEL : 민감도 순
                    - SCENT : 향 순
                    - ABSORBENCY : 흡수력 순
                    - ADHESION : 접착력 순
                    """
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CursorPageResponse<ProductItem>> getPersonalRanking(
            @RequestParam ProductKeywordType keyword,
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(productService.getPersonalRankingPage(memberId, keyword, cursor, size));
    }
}
