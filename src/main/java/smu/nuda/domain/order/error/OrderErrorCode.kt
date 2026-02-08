package smu.nuda.domain.order.error

import smu.nuda.global.error.ErrorCode

enum class OrderErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {
    INVALID_PRODUCT(
        "ORDER_INVALID_PRODUCT","주문하신 상품 중 일부를 찾을 수 없습니다. 장바구니를 확인해 주세요."
    ),
    INVALID_ORDER_STATUS(
        "ORDER_INVALID_STATUS", "해당 주문은 결제 가능한 상태가 아닙니다."
    ),
    ORDER_NOT_FOUND(
        "ORDER_NOT_FOUND", "주문 정보가 존재하지 않습니다."
    ),

    ;
}