package smu.nuda.domain.payment.error

import smu.nuda.global.error.ErrorCode

enum class PaymentErrorCode (
    override val code: String,
    override val message: String
): ErrorCode {
    INVALID_PAYMENT_STATUS(
        "PAYMENT_INVALID_PAYMENT_STATUS","해당 주문은 결제 가능한 상태가 아닙니다."
    ),
    ORDER_NOT_FOUND(
        "PAYMENT_ORDER_NOT_FOUND", "존재하지 않는 주문입니다."
    ),
    ALREADY_REQUESTED(
        "PAYMENT_ALREADY_REQUESTED", "이미 결제가 진행 중이거나 완료된 주문입니다."
    ),
    PAYMENT_NOT_FOUND(
        "PAYMENT_NOT_FOUND", "결제 내역을 찾을 수 없습니다."
    ),
    ALREADY_PAID(
        "PAYMENT_ALREADY_PAID", "이미 결제가 완료된 주문입니다."
    ),
    ORDER_MISMATCH(
        "PAYMENT_ORDER_MISMATCH", "결제 요청된 주문 정보가 일치하지 않습니다."
    ),
    INVALID_AMOUNT(
        "PAYMENT_INVALID_AMOUNT", "결제 요청 금액과 실제 주문 금액이 일치하지 않습니다."
    ),
    PAYMENT_FAILED(
        "PAYMENT_FAILED", "결제 승인에 실패하였습니다."
    ),
    NOT_ORDER_OWNER(
        "PAYMENT_NOT_ORDER_OWNER", "주문자 본인만 결제를 진행할 수 있습니다."
    ),

    ;
}