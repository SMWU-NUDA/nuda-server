package smu.nuda.domain.review.error

import smu.nuda.global.error.ErrorCode

enum class ReviewErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_REVIEW(
        "REVIEW_INVALID_REVIEW","유효하지 않은 리뷰입니다."
    ),

    ;
}