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
                responseCode = "500",
                description = "서버 내부 오류",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(
                                name = "internalServerError",
                                summary = "예상하지 못한 서버 오류",
                                value = """
                                        {
                                          "success": false,
                                          "code": "COMMON_INTERNAL_ERROR",
                                          "message": "서버 내부 오류가 발생했습니다",
                                          "data": null
                                        }
                                        """
                        )
                )
        )
})
public @interface CommonServerErrorDocs {
}
