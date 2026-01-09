package smu.nuda.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.review.dto.ReviewCreateRequest;
import smu.nuda.domain.review.dto.ReviewDetailResponse;
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
}
