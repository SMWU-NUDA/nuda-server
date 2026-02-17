package smu.nuda.domain.file.error

import smu.nuda.global.error.ErrorCode

enum class FileErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {

    EXCEED_MAX_UPLOAD_COUNT(
        "FILE_EXCEED_MAX_UPLOAD_COUNT", "업로드 가능한 최대 파일 개수를 초과했습니다."
    ),
    INVALID_CONTENT_TYPE(
        "FILE_INVALID_CONTENT_TYPE", "허용되지 않은 파일 형식입니다."
    ),
    EMPTY_FILE_LIST(
        "FILE_EMPTY_FILE_LIST","업로드할 파일이 선택되지 않았습니다."
    ),
    UPLOAD_TIMEOUT(
        "FILE_UPLOAD_TIMEOUT", "파일 업로드가 취소되었습니다."
    ),
    UPLOAD_FAILED(
        "FILE_UPLOAD_FAILED", "파일 업로드 중 오류가 발생했습니다."
    ),

    ;
}