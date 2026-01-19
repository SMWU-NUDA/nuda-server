package smu.nuda.domain.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.file.dto.PresignedUrlRequest;
import smu.nuda.domain.file.dto.PresignedUrlResponse;
import smu.nuda.domain.file.service.PresignedUploadService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/uploads")
@Tag(name = "[FILE] 파일 업로드 API")
public class UploadController {
    private final PresignedUploadService presignedUploadService;

    @PostMapping("/presigned-urls")
    @Operation(
            summary = "S3 Presigned URL 발급",
            description = "이미지 업로드를 위해 S3 Presigned URL을 발급합니다. "
                    + "요청한 파일 개수만큼 URL이 생성되며 "
                    + "업로드 완료 후 해당 URL을 포함하여 후속 API를 호출해야 합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<List<PresignedUrlResponse>> createPresignedUrls(@RequestBody PresignedUrlRequest request) {
        return ApiResponse.success(presignedUploadService.create(request));
    }
}
