package smu.nuda.domain.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.cart.dto.CartItemQuantityRequest;
import smu.nuda.domain.cart.dto.CartItemDeleteRequest;
import smu.nuda.domain.cart.dto.CartProductResponse;
import smu.nuda.domain.cart.dto.CartResponse;
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
    public ApiResponse<CartProductResponse> addProduct(@PathVariable Long productId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(cartService.addProduct(member.getId(), productId));
    }

    @GetMapping
    @Operation(
            summary = "장바구니 상품 전체 조회",
            description = "장바구니에 담긴 전체 상품을 브랜드 별로 그룹지어 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CartResponse> getCart() {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(cartService.getCart(member));
    }

    @PatchMapping("/cart/items/{cartItemId}")
    @Operation(
            summary = "장바구니 상품 수량 수정",
            description = "장바구니에 담긴 특정 상품의 수량을 증감시킵니다. " +
                    "**delta** 값에 양수(1)를 넣으면 증가, 음수(-1)를 넣으면 감소합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CartProductResponse> changeQuantity(@PathVariable Long cartItemId, @RequestBody CartItemQuantityRequest request) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(cartService.changeQuantity(cartItemId, request.getDelta(), member));
    }

    @DeleteMapping("/items")
    @Operation(
            summary = "장바구니 선택 상품 삭제",
            description = "체크박스에 선택된 상품을 삭제합니다. 체크박스 선택된 상품의 cartItem을 리스트로 요청해주세요."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<String> deleteSelectedItem(@RequestBody CartItemDeleteRequest request) {
        Member member = authenticationGuard.currentMember();
        cartService.deleteItems(request.getCartItemIds(), member);
        return ApiResponse.success("선택한 상품이 장바구니에서 삭제되었습니다.");
    }

}
