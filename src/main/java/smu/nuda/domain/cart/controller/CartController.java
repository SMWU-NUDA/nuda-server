package smu.nuda.domain.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.cart.dto.CartItemResponse;
import smu.nuda.domain.cart.service.CartService;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@Tag(name = "[CART] 장바구니 API", description = "정바구니 관련 API")
public class CartController {

    private final CartService cartService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/items/{productId}")
    @Operation(
            summary = "장바구니 상품 추가",
            description = "장바구니에 새로운 상품을 추가한다. 같은 상품이 이미 존재하면 수량을 1 증가시킨다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CartItemResponse> addProduct(@PathVariable Long productId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(cartService.addProduct(member.getId(), productId));
    }
}
