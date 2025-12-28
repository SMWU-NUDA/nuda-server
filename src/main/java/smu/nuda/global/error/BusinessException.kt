package smu.nuda.global.error

class BusinessException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)