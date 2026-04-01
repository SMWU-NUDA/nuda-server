package smu.nuda.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.common.dto.CsvUploadResponse;
import smu.nuda.domain.review.service.ReviewAdminService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.swagger.annotation.AuthForbiddenErrorDocs;
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.annotation.FileUploadErrorDocs;
import smu.nuda.global.swagger.schema.CsvErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reviews")
@Tag(name = "[ADMIN] 리뷰 관리 API")
public class AdminReviewController {

    private final ReviewAdminService reviewAdminService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping(
            value = "",
            consumes = "multipart/form-data"
    )
    @Operation(
            summary = "리뷰 CSV 일괄 등록",
            description = "CSV 파일을 업로드하여 여러 리뷰 데이터를 한 번에 등록합니다." +
                    "작성자는 csvAdmin 계정으로 저장합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "CSV 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CsvErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "csvMissingRequiredField",
                                            summary = "필수 컬럼 누락",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_MISSING_REQUIRED_FIELD",
                                                      "message": "필수 컬럼이 누락되었습니다.",
                                                      "data": "CSV 2번째 줄: review_content는 필수입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "csvInvalidValue",
                                            summary = "rating 값 오류",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_INVALID_VALUE",
                                                      "message": "컬럼 값이 올바르지 않습니다.",
                                                      "data": "CSV 4번째 줄: rating은 0~5 사이여야 합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "csvInvalidReference",
                                            summary = "매핑 상품 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_INVALID_REFERENCE",
                                                      "message": "참조 대상이 존재하지 않습니다.",
                                                      "data": "CSV 6번째 줄: 매핑되는 상품이 존재하지 않습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    @AuthForbiddenErrorDocs
    @FileUploadErrorDocs
    @CommonServerErrorDocs
    public ApiResponse<CsvUploadResponse> uploadProducts(
            @Parameter(
                    description = "업로드할 리뷰 CSV 파일",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("csvFile") MultipartFile csvFile,
            @Parameter(description = "true면 실제 저장 없이 검증만 수행") @RequestParam(defaultValue = "false") boolean dryRun
    ) {
        CsvUploadResponse res = reviewAdminService.uploadReviewsByCsv(csvFile, dryRun);

        if (dryRun) {
            return ApiResponse.success(res, "dry-run 모드로 실행되었습니다. 실제 저장은 되지 않았습니다.");
        }
        return ApiResponse.success(res);
    }
}
