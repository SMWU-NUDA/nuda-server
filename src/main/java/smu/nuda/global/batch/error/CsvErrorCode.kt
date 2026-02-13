package smu.nuda.global.batch.error

import smu.nuda.global.error.ErrorCode

enum class CsvErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    CSV_MISSING_REQUIRED_FIELD(
        "CSV_MISSING_REQUIRED_FIELD", "필수 컬럼이 누락되었습니다."
    ),
    CSV_INVALID_VALUE(
        "CSV_INVALID_VALUE", "컬럼 값이 올바르지 않습니다."
    ),
    CSV_INVALID_REFERENCE(
        "CSV_INVALID_REFERENCE", "참조 대상이 존재하지 않습니다."
    ),
    CSV_DUPLICATE_VALUE(
        "CSV_DUPLICATE_VALUE", "CSV 파일 내에 중복된 값이 포함되어 있습니다."
    ),
    ;
}
