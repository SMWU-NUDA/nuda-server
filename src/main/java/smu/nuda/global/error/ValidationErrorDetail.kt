package smu.nuda.global.error

data class ValidationErrorDetail(
    val field: String,
    val rejectedValue: Any?,
    val reason: String
)