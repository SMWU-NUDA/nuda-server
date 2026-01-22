package smu.nuda.domain.cart.error

import smu.nuda.global.error.ErrorCode

enum class CartErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {
    INVALID_QUANTITY(
        "CART_INVALID_QUANTITY", "수량은 1개 이상이어야 합니다."
    ),
    ;
}