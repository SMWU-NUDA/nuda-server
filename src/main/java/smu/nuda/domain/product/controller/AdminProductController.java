package smu.nuda.domain.product.controller;

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
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.common.dto.CsvUploadResponse;
import smu.nuda.domain.product.service.ProductAdminService;
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
@RequestMapping("/admin/products")
@Tag(name = "[ADMIN] 상품 관리 API")
public class AdminProductController {

    private final ProductAdminService productAdminService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping(
            value = "",
            consumes = "multipart/form-data"
    )
    @Operation(
            summary = "상품 CSV 일괄 등록",
            description = "CSV 파일을 업로드하여 여러 상품 데이터를 한 번에 등록합니다."
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
                                                      "data": "CSV 2번째 줄: brand_name은 필수입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "csvInvalidValue",
                                            summary = "숫자 형식 오류",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_INVALID_VALUE",
                                                      "message": "컬럼 값이 올바르지 않습니다.",
                                                      "data": "CSV 3번째 줄: average_rating은 숫자여야 합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "csvInvalidReference",
                                            summary = "브랜드 또는 카테고리 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_INVALID_REFERENCE",
                                                      "message": "참조 대상이 존재하지 않습니다.",
                                                      "data": "CSV 4번째 줄: 존재하지 않는 브랜드입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "csvDuplicateValue",
                                            summary = "중복 external_product_id",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_DUPLICATE_VALUE",
                                                      "message": "CSV 파일 내에 중복된 값이 포함되어 있습니다.",
                                                      "data": "CSV 7번째 줄: CSV 내 external_product_id 중복입니다."
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
                    description = "업로드할 상품 CSV 파일",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("csvFile") MultipartFile csvFile,
            @Parameter(description = "true면 실제 저장 없이 검증만 수행") @RequestParam(defaultValue = "false") boolean dryRun
    ) {
        CsvUploadResponse res = productAdminService.uploadProductsByCsv(csvFile, dryRun);

        if (dryRun) {
            return ApiResponse.success(res, "dry-run 모드로 실행되었습니다. 실제 저장은 되지 않았습니다.");
        }
        return ApiResponse.success(res);
    }

    @PostMapping(
            value = "/content-images",
            consumes = "multipart/form-data"
    )
    @Operation(
            summary = "상품 이미지 CSV 일괄 등록",
            description = "CSV 파일을 업로드하여 여러 상품의 이미지를 한 번에 등록합니다."
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
                                                      "data": "CSV 2번째 줄: content는 필수입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "csvInvalidImageType",
                                            summary = "유효하지 않은 이미지 타입",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_INVALID_IMAGE_TYPE",
                                                      "message": "유효하지 않은 이미지 타입입니다.",
                                                      "data": "CSV 3번째 줄: 허용된 타입: MAIN, CONTENT"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "csvInvalidContentFormat",
                                            summary = "content JSON 형식 오류",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_INVALID_CONTENT_FORMAT",
                                                      "message": "content 형식이 올바르지 않습니다. JSON 배열 형식이어야 합니다.",
                                                      "data": "CSV 5번째 줄: content 형식이 올바르지 않습니다. JSON 배열 형식이어야 합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "csvInvalidReference",
                                            summary = "존재하지 않는 상품",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CSV_INVALID_REFERENCE",
                                                      "message": "참조 대상이 존재하지 않습니다.",
                                                      "data": "CSV 6번째 줄: 존재하지 않는 상품입니다."
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
    public ApiResponse<CsvUploadResponse> uploadContentImages(
            @Parameter(
                    description = "업로드할 상품 이미지 CSV 파일",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("csvFile") MultipartFile csvFile,
            @Parameter(description = "true면 실제 저장 없이 검증만 수행") @RequestParam(defaultValue = "false") boolean dryRun
    ) {
        CsvUploadResponse res = productAdminService.uploadContentImagesByCsv(csvFile, dryRun);

        if (dryRun) {
            return ApiResponse.success(res, "dry-run 모드로 실행되었습니다. 실제 저장은 되지 않았습니다.");
        }
        return ApiResponse.success(res);
    }

}
