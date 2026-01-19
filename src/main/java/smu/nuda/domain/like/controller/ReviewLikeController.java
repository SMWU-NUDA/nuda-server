package smu.nuda.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.like.dto.LikeToggleResponse;
import smu.nuda.domain.like.service.LikeService;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "[REVIEW LIKE] 리뷰 좋아요 API")
public class ReviewLikeController {

    private final LikeService likeService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/{reviewId}/likes")
    @Operation(
            summary = "리뷰 좋아요",
            description = "로그인한 사용자가 리뷰를 좋아요하거나 취소합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<LikeToggleResponse> toggleReviewLike(@PathVariable Long reviewId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(likeService.reviewLikeToggle(reviewId, member));
    }

}
