package smu.nuda.global.batch.exception

import smu.nuda.global.error.DomainException
import smu.nuda.global.error.ErrorCode

class CsvValidationException @JvmOverloads constructor(
    errorCode: ErrorCode,
    val rowNumber: Int,
    detailMessage: String = errorCode.message
) : DomainException(
    errorCode,
    buildMessage(rowNumber, detailMessage.ifBlank { errorCode.message })
) {
    companion object {
        @JvmStatic
        private fun buildMessage(rowNumber: Int, message: String): String =
            "CSV ${rowNumber}번째 줄: $message"
    }
}
