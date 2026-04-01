package smu.nuda.global.swagger.schema;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValidationErrorResponse", description = "유효성 검증 실패 응답 포맷")
public class ValidationErrorResponse {

    @Schema(description = "요청 성공 여부", example = "false")
    public boolean success;

    @Schema(description = "애플리케이션 에러 코드", example = "VALIDATION_INVALID_PARAMETER")
    public String code;

    @Schema(description = "에러 메시지", example = "요청 값이 올바르지 않습니다")
    public String message;

    @ArraySchema(schema = @Schema(implementation = ValidationErrorDetail.class))
    public ValidationErrorDetail[] data;
}
