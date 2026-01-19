package smu.nuda.domain.review.error

import smu.nuda.global.error.ErrorCode

enum class ReviewErrorCode (
    override val code: String,
    override val message: String
) : ErrorCode {

    INVALID_REVIEW(
        "REVIEW_INVALID_REVIEW","유효하지 않은 리뷰입니다."
    ),
    NOT_REVIEW_OWNER(
        "REVIEW_NOT_REVIEW_OWNER", "해당 리뷰에 대한 수정 및 삭제 권한이 없습니다."
    )
    ;
}