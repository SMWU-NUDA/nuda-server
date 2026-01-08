package smu.nuda.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/products")
public class ProductLikeController {

    private  final LikeService likeService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/{productId}/likes")
    @Operation(
            summary = "상품 찜하기",
            description = "로그인한 사용자가 고유번호에 해당하는 상품을 찜하거나 찜하거나 취소합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<LikeToggleResponse> toggle(@PathVariable Long productId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(likeService.productLikeToggle(productId, member));
    }
}
