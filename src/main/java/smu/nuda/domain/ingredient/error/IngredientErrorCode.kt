package smu.nuda.domain.ingredient.error

import smu.nuda.global.error.ErrorCode

enum class IngredientErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_INGREDIENT(
        "INVALID_INGREDIENT", "유효하지 않은 성분 정보입니다."
    ),
    EMPTY_PRODUCT_CONTENT(
        "EMPTY_PRODUCT_CONTENT", "상품에 등록된 성분 정보가 존재하지 않습니다."
    ),
}