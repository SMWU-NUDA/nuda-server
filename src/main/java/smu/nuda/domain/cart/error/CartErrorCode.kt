package smu.nuda.domain.cart.error

import smu.nuda.global.error.ErrorCode

enum class CartErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {
    INVALID_QUANTITY(
        "CART_INVALID_QUANTITY", "수량은 1개 이상이어야 합니다."
    ),
    LOCK_TIMEOUT(
        "CART_LOCK_TIMEOUT", "현재 요청이 많아 장바구니를 업데이트할 수 없습니다. 잠시 후 다시 시도해주세요.",
    ),
    CART_NOT_FOUND(
        "CART_NOT_FOUND","장바구니 정보를 찾을 수 없습니다."
    ),
    INVALID_CART_ITEM(
        "CART_INVALID_CART_ITEM","존재하지 않거나 유효하지 않은 장바구니 아이템 ID입니다."
    ),
    NOT_MY_CART_ITEM(
        "CART_NOT_MY_CART_ITEM", "해당 계정의 장바구니에 담긴 상품이 아닙니다."
    ),
    QUANTITY_MISMATCH(
        "ORDER_QUANTITY_MISMATCH","선택하신 상품 수량과 주문하려는 수량이 일치하지 않습니다."
    ),

    ;
}