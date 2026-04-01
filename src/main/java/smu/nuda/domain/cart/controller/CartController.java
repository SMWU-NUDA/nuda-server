package smu.nuda.domain.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
@Tag(name = "[CART] 장바구니 API", description = "장바구니 관련 API")
@CommonServerErrorDocs
public class CartController {

    private final CartService cartService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/items/{productId}")
    @Operation(
            summary = "장바구니 상품 추가",
            description = "장바구니에 새로운 상품을 추가한다. 같은 상품이 이미 존재하면 수량을 1 증가시킨다."
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
                                            name = "cartNotFound",
                                            summary = "장바구니 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_NOT_FOUND",
                                                      "message": "장바구니 정보를 찾을 수 없습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "lockTimeout",
                                            summary = "장바구니 잠금 타임아웃",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_LOCK_TIMEOUT",
                                                      "message": "현재 요청이 많아 장바구니를 업데이트할 수 없습니다. 잠시 후 다시 시도해주세요.",
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
    public ApiResponse<CartProductResponse> addProduct(@PathVariable Long productId) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(cartService.addProduct(memberId, productId));
    }

    @GetMapping
    @Operation(
            summary = "장바구니 상품 전체 조회",
            description = "장바구니에 담긴 전체 상품을 브랜드 별로 그룹지어 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<CartResponse> getCart() {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(cartService.getCart(memberId));
    }

    @PatchMapping("/items/{cartItemId}")
    @Operation(
            summary = "장바구니 상품 수량 수정",
            description = "장바구니에 담긴 특정 상품의 수량을 증감시킵니다. " +
                    "**delta** 값에 양수(1)를 넣으면 증가, 음수(-1)를 넣으면 감소합니다."
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
                                            name = "invalidCartItem",
                                            summary = "유효하지 않은 장바구니 아이템",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_INVALID_CART_ITEM",
                                                      "message": "존재하지 않거나 유효하지 않은 장바구니 아이템 ID입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "notMyCartItem",
                                            summary = "본인 장바구니 아이템 아님",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_NOT_MY_CART_ITEM",
                                                      "message": "해당 계정의 장바구니에 담긴 상품이 아닙니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
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
                                            name = "invalidQuantity",
                                            summary = "유효하지 않은 수량",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_INVALID_QUANTITY",
                                                      "message": "수량은 1개 이상이어야 합니다.",
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
    public ApiResponse<CartProductResponse> changeQuantity(@PathVariable Long cartItemId, @RequestBody CartItemQuantityRequest request) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(cartService.changeQuantity(cartItemId, request.getDelta(), member));
    }

    @DeleteMapping("/items")
    @Operation(
            summary = "장바구니 선택 상품 삭제",
            description = "체크박스에 선택된 상품을 삭제합니다. 체크박스 선택된 상품의 cartItem을 리스트로 요청해주세요."
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
                                            name = "invalidCartItem",
                                            summary = "유효하지 않은 장바구니 아이템",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_INVALID_CART_ITEM",
                                                      "message": "존재하지 않거나 유효하지 않은 장바구니 아이템 ID입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "notMyCartItem",
                                            summary = "본인 장바구니 아이템 아님",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_NOT_MY_CART_ITEM",
                                                      "message": "해당 계정의 장바구니에 담긴 상품이 아닙니다.",
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
    public ApiResponse<String> deleteSelectedItems(@RequestBody CartItemDeleteRequest request) {
        Member member = authenticationGuard.currentMember();
        cartService.deleteSelectedItems(request.getCartItemIds(), member);
        return ApiResponse.success("선택한 상품이 장바구니에서 삭제되었습니다.");
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(
            summary = "장바구니 상품 단건 삭제",
            description = "상품 옆 [X] 버튼을 눌렀을 때 특정 상품을 삭제합니다."
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
                                            name = "invalidCartItem",
                                            summary = "유효하지 않은 장바구니 아이템",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_INVALID_CART_ITEM",
                                                      "message": "존재하지 않거나 유효하지 않은 장바구니 아이템 ID입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "notMyCartItem",
                                            summary = "본인 장바구니 아이템 아님",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_NOT_MY_CART_ITEM",
                                                      "message": "해당 계정의 장바구니에 담긴 상품이 아닙니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
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
                                    )
                            }
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<String> deleteCartItem(@PathVariable Long cartItemId) {
        Member member = authenticationGuard.currentMember();
        cartService.deleteItem(cartItemId, member);
        return ApiResponse.success("선택한 상품이 장바구니에서 삭제되었습니다.");
    }

    @DeleteMapping("/items/all")
    @Operation(
            summary = "장바구니 전체 상품 삭제",
            description = "장바구니에 담긴 전체 상품을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "cartNotFound",
                                    summary = "장바구니 없음",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "CART_NOT_FOUND",
                                              "message": "장바구니 정보를 찾을 수 없습니다.",
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
    public ApiResponse<String> clearCart() {
        Member member = authenticationGuard.currentMember();
        cartService.clearCart(member);
        return ApiResponse.success("장바구니에 담긴 모든 상품이 삭제되었습니다.");
    }

}
