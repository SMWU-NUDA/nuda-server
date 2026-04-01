package smu.nuda.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.like.dto.LikeToggleResponse;
import smu.nuda.domain.like.dto.LikedProductResponse;
import smu.nuda.domain.like.service.LikeService;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.annotation.ValidationBadRequestDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "[PRODUCT LIKE] 생리대 상품 찜하기 API")
@CommonServerErrorDocs
public class ProductLikeController {

    private  final LikeService likeService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/{productId}/likes")
    @Operation(
            summary = "상품 찜하기",
            description = "로그인한 사용자가 고유번호에 해당하는 상품을 찜하거나 찜하거나 취소합니다."
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
    public ApiResponse<LikeToggleResponse> toggle(@PathVariable Long productId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(likeService.productLikeToggle(productId, member));
    }

    @GetMapping("/likes")
    @Operation(
            summary = "사용자의 찜한 상품 조회",
            description = "로그인한 사용자가 찜한 전체 상품 목록을 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    @ValidationBadRequestDocs
    public ApiResponse<CursorPageResponse<LikedProductResponse>> getLikedProducts(
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(likeService.likedProducts(memberId, cursor, size));
    }

}
