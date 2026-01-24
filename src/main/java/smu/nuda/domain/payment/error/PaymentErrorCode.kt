package smu.nuda.domain.payment.error

import smu.nuda.global.error.ErrorCode

enum class PaymentErrorCode (
    override val code: String,
    override val message: String
): ErrorCode {
    INVALID_STATUS(
        "PAYMENT_INVALID_STATUS","현재 상태가 결제 승인 가능한 단계가 아닙니다."
    )
}