package smu.nuda.global.error

open class DomainException @JvmOverloads constructor(
    val errorCode: ErrorCode,
    val data: Any? = null
) : RuntimeException(errorCode.message)
