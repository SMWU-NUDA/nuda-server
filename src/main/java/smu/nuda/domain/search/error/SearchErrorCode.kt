package smu.nuda.domain.search.error

import smu.nuda.global.error.ErrorCode

enum class SearchErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    KEYWORD_TOO_SHORT("SEARCH_KEYWORD_TOO_SHORT", "검색어는 2글자 이상 입력해주세요."),
    SUGGEST_RATE_LIMIT_EXCEEDED("SEARCH_SUGGEST_RATE_LIMIT_EXCEEDED", "요청이 너무 빠릅니다. 잠시 후 다시 시도해주세요.")
}
