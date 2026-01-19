package smu.nuda.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.review.dto.MyReviewResponse;
import smu.nuda.domain.review.dto.ReviewCreateRequest;
import smu.nuda.domain.review.dto.ReviewDetailResponse;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.guard.ReviewGuard;
import smu.nuda.domain.review.service.ReviewService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "[REVIEW] 리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthenticationGuard authenticationGuard;
    private final ReviewGuard reviewGuard;

    @PostMapping
    @Operation(
            summary = "리뷰 작성",
            description = "특정 상품에 대한 리뷰를 작성합니다. 이미지를 presigned URL로 요청해주세요."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<ReviewDetailResponse> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(reviewService.create(request, member));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(
            summary = "리뷰 삭제",
            description = "사용자가 작성한 리뷰를 삭제합니다. " +
                    "본인이 작성한 리뷰만 삭제 가능하며, 성공 시 해당 리뷰 데이터가 완전히 제거됩니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<String> deleteReview(@PathVariable Long reviewId) {
        Member member = authenticationGuard.currentMember();
        Review review = reviewGuard.validateDeletable(reviewId, member);
        reviewService.delete(review);
        return ApiResponse.success("해당 리뷰가 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/me")
    @Operation(
            summary = "나의 리뷰 목록 조회",
            description = "현재 로그인한 사용자가 작성한 리뷰들을 최신순으로 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CursorPageResponse<MyReviewResponse>> myReviews(
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(reviewService.getMyReviews(member, cursor, size));
    }

}
