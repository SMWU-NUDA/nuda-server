package smu.nuda.domain.file.error

import smu.nuda.global.error.ErrorCode

enum class FileErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    EXCEED_MAX_UPLOAD_COUNT(
        "FILE_EXCEED_MAX_UPLOAD_COUNT", "업로드 가능한 최대 파일 개수를 초과했습니다."
    ),
    ;
}