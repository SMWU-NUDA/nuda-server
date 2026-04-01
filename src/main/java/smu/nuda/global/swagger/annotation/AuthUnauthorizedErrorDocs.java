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
                responseCode = "401",
                description = "인증 실패",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "authRequired",
                                        summary = "로그인 필요",
                                        value = """
                                                {
                                                  "success": false,
                                                  "code": "AUTH_REQUIRED",
                                                  "message": "로그인이 필요한 기능입니다.",
                                                  "data": null
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "invalidAccessToken",
                                        summary = "유효하지 않은 액세스 토큰",
                                        value = """
                                                {
                                                  "success": false,
                                                  "code": "AUTH_INVALID_ACCESS_TOKEN",
                                                  "message": "유효하지 않은 액세스 토큰입니다.",
                                                  "data": null
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "expiredToken",
                                        summary = "만료된 토큰",
                                        value = """
                                                {
                                                  "success": false,
                                                  "code": "AUTH_EXPIRED_TOKEN",
                                                  "message": "해당 토큰이 만료되었습니다.",
                                                  "data": null
                                                }
                                                """
                                )
                        }
                )
        )
})
public @interface AuthUnauthorizedErrorDocs {
}
