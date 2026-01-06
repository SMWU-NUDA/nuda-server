package smu.nuda.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.dto.ProductUploadResponse;
import smu.nuda.domain.product.service.ProductAdminService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "[PRODUCT] 생리대 상품 API")
public class ProductController {

    private final ProductAdminService productAdminService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/admin/products")
    @Operation(
            summary = "상품 CSV 일괄 등록",
            description = "CSV 파일을 업로드하여 여러 상품 데이터를 한 번에 등록합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<ProductUploadResponse> uploadProducts(@RequestPart MultipartFile file) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(productAdminService.uploadProductsByCsv(file, member));
    }

}
