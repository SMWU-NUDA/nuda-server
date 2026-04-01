package smu.nuda.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "[REVIEW LIKE] 리뷰 좋아요 API")
@CommonServerErrorDocs
public class ReviewLikeController {

    private final LikeService likeService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/{reviewId}/likes")
    @Operation(
            summary = "리뷰 좋아요",
            description = "로그인한 사용자가 리뷰를 좋아요하거나 취소합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
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
                            )
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<LikeToggleResponse> toggleReviewLike(@PathVariable Long reviewId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(likeService.reviewLikeToggle(reviewId, member));
    }

}
