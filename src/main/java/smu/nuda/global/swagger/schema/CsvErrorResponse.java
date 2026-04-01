package smu.nuda.global.swagger.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CsvErrorResponse", description = "CSV 업로드 검증 실패 응답 포맷")
public class CsvErrorResponse {

    @Schema(description = "요청 성공 여부", example = "false")
    public boolean success;

    @Schema(description = "애플리케이션 에러 코드", example = "CSV_INVALID_REFERENCE")
    public String code;

    @Schema(description = "에러 메시지", example = "참조 대상이 존재하지 않습니다.")
    public String message;

    @Schema(
            description = "CSV 행 번호와 상세 실패 원인을 포함한 문자열",
            example = "CSV 4번째 줄: 존재하지 않는 브랜드입니다."
    )
    public String data;
}
