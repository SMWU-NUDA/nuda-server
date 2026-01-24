package smu.nuda.domain.product.error

import smu.nuda.global.error.ErrorCode

enum class ProductErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_EXTERNAL_PRODUCT_ID(
        "PRODUCT_INVALID_EXTERNAL_PRODUCT_ID", "유효하지 않은 외부 상품 식별자입니다."
    ),
    INVALID_PRODUCT(
        "PRODUCT_INVALID", "유효하지 않은 상품입니다."
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
    ),
    INVALID_REVIEW_COUNT(
      "PRODUCT_INVALID_REVIEW_COUNT","리뷰 개수 정보가 올바르지 않습니다."
    ),
    INVALID_AVERAGE_RATING(
      "PRODUCT_INVALID_AVERAGE_RATING","평균 평점 정보가 올바르지 않습니다."
    ),

    ;
}
