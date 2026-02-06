package smu.nuda.domain.payment.error

import smu.nuda.global.error.ErrorCode

enum class PaymentErrorCode (
    override val code: String,
    override val message: String
): ErrorCode {
    INVALID_ORDER_STATUS(
        "PAYMENT_INVALID_ORDER_STATUS","해당 주문은 결제 가능한 상태가 아닙니다."
    ),
    ORDER_NOT_FOUND(
        "PAYMENT_ORDER_NOT_FOUND", "존재하지 않는 주문입니다."
    ),
    ALREADY_REQUESTED(
        "PAYMENT_ALREADY_REQUESTED", "이미 결제가 진행 중이거나 완료된 주문입니다."
    )

    ;
}