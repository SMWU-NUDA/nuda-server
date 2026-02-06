package smu.nuda.domain.order.error

import smu.nuda.global.error.ErrorCode

enum class OrderErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {
    ORDER_ITEM_REQUIRED(
        "ORDER_ITEM_REQUIRED", "장바구니가 비어 있어 주문을 진행할 수 없습니다."
    ),
    INVALID_CART_ITEM(
        "ORDER_INVALID_CART_ITEM","유효하지 않은 장바구니 상품입니다."
    ),
    INVALID_QUANTITY(
        "ORDER_INVALID_QUANTITY", "상품 수량이 올바르지 않습니다."
    ),
    INVALID_TOTAL_AMOUNT(
        "ORDER_INVALID_TOTAL_AMOUNT", "주문 금액이 일치하지 않습니다."
    ),
    INVALID_PRODUCT(
        "ORDER_INVALID_PRODUCT","주문하신 상품 중 일부를 찾을 수 없습니다. 장바구니를 확인해 주세요."
    ),

    ;
}