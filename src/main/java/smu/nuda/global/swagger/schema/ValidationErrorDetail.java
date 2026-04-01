package smu.nuda.global.swagger.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValidationErrorDetail", description = "유효성 검증 실패 상세 항목")
public class ValidationErrorDetail {

    @Schema(description = "오류가 발생한 필드명", example = "password")
    public String field;

    @Schema(description = "거부된 값", example = "")
    public Object rejectedValue;

    @Schema(description = "검증 실패 사유", example = "유효하지 않은 값입니다")
    public String reason;
}
