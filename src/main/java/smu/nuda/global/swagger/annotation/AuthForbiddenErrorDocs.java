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
                responseCode = "403",
                description = "권한 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                name = "accessDenied",
                                summary = "관리자 권한 없음",
                                value = """
                                        {
                                          "success": false,
                                          "code": "AUTH_ACCESS_DENIED",
                                          "message": "해당 계정은 요청 권한이 없습니다.",
                                          "data": null
                                        }
                                        """
                        )
                )
        )
})
public @interface AuthForbiddenErrorDocs {
}
