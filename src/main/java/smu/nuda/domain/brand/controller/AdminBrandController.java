package smu.nuda.domain.brand.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.brand.service.BrandAdminService;
import smu.nuda.domain.common.dto.CsvUploadResponse;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

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
    @SecurityRequirement(name = "JWT")
    @LoginRequired
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
        Member member = authenticationGuard.currentMember();
        CsvUploadResponse res = brandAdminService.uploadBrandsByCsv(csvFile, dryRun);

        if (dryRun) {
            return ApiResponse.success(res, "dry-run 모드로 실행되었습니다. 실제 저장은 되지 않았습니다.");
        }
        return ApiResponse.success(res);
    }

}
