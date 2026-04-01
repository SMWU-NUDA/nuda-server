package smu.nuda.global.swagger.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import smu.nuda.global.swagger.schema.ErrorResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "400",
                description = "파일 업로드 오류",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                name = "fileUploadFailed",
                                summary = "파일 업로드 실패",
                                value = """
                                        {
                                          "success": false,
                                          "code": "FILE_UPLOAD_FAILED",
                                          "message": "파일 업로드 중 오류가 발생했습니다.",
                                          "data": null
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "408",
                description = "파일 업로드 타임아웃",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                name = "fileUploadTimeout",
                                summary = "업로드 중 연결 종료",
                                value = """
                                        {
                                          "success": false,
                                          "code": "FILE_UPLOAD_TIMEOUT",
                                          "message": "파일 업로드가 취소되었습니다.",
                                          "data": null
                                        }
                                        """
                        )
                )
        )
})
public @interface FileUploadErrorDocs {
}
