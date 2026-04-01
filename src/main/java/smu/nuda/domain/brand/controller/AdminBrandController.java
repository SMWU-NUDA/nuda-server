package smu.nuda.domain.brand.controller;

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
import smu.nuda.domain.brand.service.BrandAdminService;
import smu.nuda.domain.common.dto.CsvUploadResponse;
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
@RequestMapping("/admin/brands")
@Tag(name = "[ADMIN] 브랜드 관리 API")
public class AdminBrandController {

    private final BrandAdminService brandAdminService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping(
            value = "",
            consumes = "multipart/form-data"
    )
    @Operation(
            summary = "브랜드 CSV 일괄 등록",
            description = "CSV 파일을 업로드하여 여러 브랜드 데이터를 한 번에 등록합니다."
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
                                                      "data": "CSV 2번째 줄: name은 필수입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "csvDuplicateValue",
                                            summary = "CSV 내부 브랜드명 중복",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_DUPLICATE_VALUE",
                                                      "message": "CSV 파일 내에 중복된 값이 포함되어 있습니다.",
                                                      "data": "CSV 5번째 줄: CSV 내부에 중복된 브랜드명이 존재합니다."
                                                    }
                                                    """
                                    ),
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
                    description = "업로드할 브랜드 CSV 파일",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("csvFile") MultipartFile csvFile,
            @Parameter(description = "true면 실제 저장 없이 검증만 수행") @RequestParam(defaultValue = "false") boolean dryRun
    ) {
        CsvUploadResponse res = brandAdminService.uploadBrandsByCsv(csvFile, dryRun);

        if (dryRun) {
            return ApiResponse.success(res, "dry-run 모드로 실행되었습니다. 실제 저장은 되지 않았습니다.");
        }
        return ApiResponse.success(res);
    }

}
