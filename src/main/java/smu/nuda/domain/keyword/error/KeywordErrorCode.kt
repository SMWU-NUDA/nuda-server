package smu.nuda.domain.keyword.error

import smu.nuda.global.error.ErrorCode

enum class KeywordErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

}
