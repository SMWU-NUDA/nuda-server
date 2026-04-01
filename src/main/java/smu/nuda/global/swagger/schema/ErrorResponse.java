package smu.nuda.global.swagger.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "일반 에러 응답 포맷")
public class ErrorResponse {

    @Schema(description = "요청 성공 여부", example = "false")
    public boolean success;

    @Schema(description = "애플리케이션 에러 코드", example = "AUTH_REQUIRED")
    public String code;

    @Schema(description = "에러 메시지", example = "로그인이 필요한 기능입니다.")
    public String message;

    @Schema(description = "추가 데이터. 일반 에러에서는 보통 null 입니다.", nullable = true)
    public Object data;
}
