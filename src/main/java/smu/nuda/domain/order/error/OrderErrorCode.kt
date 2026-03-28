package smu.nuda.domain.order.error

import smu.nuda.global.error.ErrorCode

enum class OrderErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {
    INVALID_PRODUCT(
        "ORDER_INVALID_PRODUCT", "장바구니에 담긴 일부 상품을 찾을 수 없습니다."
    ),
    DIRECT_INVALID_PRODUCT(
        "ORDER_DIRECT_INVALID_PRODUCT", "바로 결제할 상품을 찾을 수 없습니다."
    ),
    INVALID_ORDER_STATUS(
        "ORDER_INVALID_STATUS", "해당 주문은 결제 가능한 상태가 아닙니다."
    ),
    ORDER_NOT_FOUND(
        "ORDER_NOT_FOUND", "주문 정보가 존재하지 않습니다."
    ),

    ;
}