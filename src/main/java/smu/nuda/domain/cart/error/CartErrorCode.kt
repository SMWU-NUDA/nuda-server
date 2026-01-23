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
    ;
}