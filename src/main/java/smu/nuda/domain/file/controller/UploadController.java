package smu.nuda.domain.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/uploads")
@Tag(name = "[FILE] 파일 업로드 API")
@CommonServerErrorDocs
public class UploadController {
    private final PresignedUploadService presignedUploadService;

    @PostMapping("/presigned-urls")
    @Operation(
            summary = "S3 Presigned URL 발급",
            description = "이미지 업로드를 위해 S3 Presigned URL을 발급합니다. "
                    + "요청한 파일 개수만큼 URL이 생성되며 "
                    + "업로드 완료 후 해당 URL을 포함하여 후속 API를 호출해야 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "파일 업로드 요청 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "emptyFileList",
                                            summary = "업로드 파일 목록 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "FILE_EMPTY_FILE_LIST",
                                                      "message": "업로드할 파일이 선택되지 않았습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "exceedMaxUploadCount",
                                            summary = "최대 업로드 개수 초과",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "FILE_EXCEED_MAX_UPLOAD_COUNT",
                                                      "message": "업로드 가능한 최대 파일 개수를 초과했습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalidContentType",
                                            summary = "허용되지 않은 파일 형식",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "FILE_INVALID_CONTENT_TYPE",
                                                      "message": "허용되지 않은 파일 형식입니다.",
                                                      "data": null
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
    public ApiResponse<List<PresignedUrlResponse>> createPresignedUrls(@RequestBody PresignedUrlRequest request) {
        return ApiResponse.success(presignedUploadService.create(request));
    }
}
