package smu.nuda.domain.brand.error

import smu.nuda.global.error.ErrorCode

enum class BrandErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_BRAND(
        "BRAND_INVALID_BRAND", "유효하지 않은 브랜드입니다."
    ),

    ;
}