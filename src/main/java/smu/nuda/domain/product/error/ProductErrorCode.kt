package smu.nuda.domain.product.error

import smu.nuda.global.error.ErrorCode

enum class ProductErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_BRAND(
        "PRODUCT_INVALID_BRAND", "유효하지 않은 브랜드입니다."
    ),
    INVALID_CATEGORY(
        "PRODUCT_INVALID_CATEGORY", "유효하지 않은 카테고리입니다."
    ),
    INVALID_PRODUCT_NAME(
        "PRODUCT_INVALID_PRODUCT_NAME", "상품명은 필수입니다."
    ),
    INVALID_COST_PRICE(
        "PRODUCT_INVALID_COST_PRICE", "원가는 0 이상이어야 합니다."
    ),
    INVALID_DISCOUNT_RATE(
        "PRODUCT_INVALID_DISCOUNT_RATE", "할인율은 0~100 사이여야 합니다."
    );
}
