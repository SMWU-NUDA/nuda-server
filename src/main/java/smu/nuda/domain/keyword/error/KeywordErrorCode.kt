package smu.nuda.domain.keyword.error

import smu.nuda.global.error.ErrorCode

enum class KeywordErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    KEYWORD_NOT_FOUND(
        "KEYWORD_NOT_FOUND", "사용자의 키워드 정보를 찾을 수 없습니다."
    ),
}
