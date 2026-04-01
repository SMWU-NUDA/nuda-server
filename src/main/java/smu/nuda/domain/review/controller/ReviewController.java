package smu.nuda.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.review.dto.*;
import smu.nuda.domain.review.dto.enums.ReviewKeywordType;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.guard.ReviewGuard;
import smu.nuda.domain.review.service.ReviewService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.annotation.ValidationBadRequestDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "[REVIEW] 리뷰 API")
@CommonServerErrorDocs
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthenticationGuard authenticationGuard;
    private final ReviewGuard reviewGuard;

    @PostMapping("/reviews")
    @Operation(
            summary = "리뷰 작성",
            description = "특정 상품에 대한 리뷰를 작성합니다. 이미지를 presigned URL로 요청해주세요."
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
    @ValidationBadRequestDocs
    public ApiResponse<ReviewItem> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(reviewService.create(request, member));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(
            summary = "리뷰 삭제",
            description = "사용자가 작성한 리뷰를 삭제합니다. " +
                    "본인이 작성한 리뷰만 삭제 가능하며, 성공 시 해당 리뷰 데이터가 완전히 제거됩니다."
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
                                            name = "invalidReview",
                                            summary = "유효하지 않은 리뷰",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "REVIEW_INVALID_REVIEW",
                                                      "message": "유효하지 않은 리뷰입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "notReviewOwner",
                                            summary = "리뷰 삭제 권한 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "REVIEW_NOT_REVIEW_OWNER",
                                                      "message": "해당 리뷰에 대한 수정 및 삭제 권한이 없습니다.",
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
    public ApiResponse<String> deleteReview(@PathVariable Long reviewId) {
        Member member = authenticationGuard.currentMember();
        Review review = reviewGuard.validateDeletable(reviewId, member);
        reviewService.delete(review);
        return ApiResponse.success("해당 리뷰가 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/reviews/me")
    @Operation(
            summary = "나의 리뷰 목록 조회",
            description = "현재 로그인한 사용자가 작성한 리뷰들을 최신순으로 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<CursorPageResponse<MyReviewResponse>> myReviews(
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(reviewService.getMyReviews(memberId, cursor, size));
    }

    @GetMapping("/products/{productId}/reviews/global-rankings")
    @Operation(
            summary = "키워드별 전체 리뷰 랭킹 조회",
            description = """
                    키워드(자극도, 향, 흡수력 등)를 기준으로 정렬된 전체 리뷰의 랭킹을 조회합니다.
                    - DEFAULT : 전체
                    - IRRITATION_LEVEL : 민감도 순
                    - SCENT : 향 순
                    - ABSORBENCY : 흡수력 순
                    - ADHESION : 접착력 순
                    """
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
    public ApiResponse<CursorPageResponse<ReviewItem>> getGlobalRanking(
            @PathVariable Long productId,
            @RequestParam ReviewKeywordType keyword,
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(reviewService.getGlobalRankingPage(productId, memberId, keyword, cursor, size));
    }

    @GetMapping("/products/{productId}/review-summary")
    @Operation(
            summary = "리뷰 AI 요약 조회",
            description = """
                    해당 상품의 리뷰를 AI 기반으로 분석한 요약 정보를 조회합니다.
                    - 긍정/부정 키워드 Top 5
                    - 사용자 만족도 (긍정 비율 기반)
                    - 주요 리뷰 트렌드 하이라이트
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
                                            name = "reviewInsufficient",
                                            summary = "리뷰 수 부족",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "ML_REVIEW_INSUFFICIENT",
                                                      "message": "리뷰 수가 충분하지 않아 AI 분석을 제공할 수 없습니다.",
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
    @ValidationBadRequestDocs
    public ApiResponse<ReviewAiSummaryResponse> getReviewAiSummary(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") @Min(1) int topN
    ){
        return ApiResponse.success(reviewService.getReviewAiSummary(productId, topN));
    }

    @GetMapping("/products/{productId}/review-keywords")
    @Operation(
            summary = "리뷰 긍정/부정 키워드 조회",
            description = "ML 분석을 통한 해당 상품 리뷰의 긍정/부정 키워드 리스트를 조회합니다."
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
                                            name = "reviewInsufficient",
                                            summary = "리뷰 수 부족",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "ML_REVIEW_INSUFFICIENT",
                                                      "message": "리뷰 수가 충분하지 않아 AI 분석을 제공할 수 없습니다.",
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
    @ValidationBadRequestDocs
    public ApiResponse<SentimentKeywordsItem> getReviewKeywordsOnly(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "3") @Min(1) int topN
    ) {
        return ApiResponse.success(reviewService.getReviewKeywords(productId, topN));
    }
}
