package smu.nuda.global.swagger.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import smu.nuda.global.swagger.schema.ValidationErrorResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "400",
                description = "요청 값 검증 실패",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ValidationErrorResponse.class),
                        examples = @ExampleObject(
                                name = "validationInvalidParameter",
                                summary = "유효성 검증 실패",
                                value = """
                                        {
                                          "success": false,
                                          "code": "VALIDATION_INVALID_PARAMETER",
                                          "message": "요청 값이 올바르지 않습니다",
                                          "data": [
                                            {
                                              "field": "password",
                                              "rejectedValue": "",
                                              "reason": "유효하지 않은 값입니다"
                                            }
                                          ]
                                        }
                                        """
                        )
                )
        )
})
public @interface ValidationBadRequestDocs {
}
