package smu.nuda.domain.keyword.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.keyword.dto.KeywordRequest;
import smu.nuda.domain.keyword.dto.KeywordResponse;
import smu.nuda.domain.keyword.service.KeywordService;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "[KEYWORD] 키워드 API", description = "사용자의 키워드 API")
@CommonServerErrorDocs
public class KeywordController {

    private final KeywordService keywordService;
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/me/keywords")
    @Operation(
            summary = "키워드 조회",
            description = "회원가입시 설문조사로 선택한 키워드를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "keywordNotFound",
                                    summary = "키워드 정보 없음",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "KEYWORD_NOT_FOUND",
                                              "message": "사용자의 키워드 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<KeywordResponse> getMyKeyword() {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(keywordService.getMyKeyword(member));
    }

    @PatchMapping("/me/keywords")
    @Operation(
            summary = "키워드 수정",
            description = "사용자의 키워드를 업데이트합니다. 기존 키워드가 존재하지 않을 경우 에러를 반환합니다." +
                    "모든 필드를 보낼 필요 없이, 변경을 원하는 항목만 선택적으로 포함하여 수정이 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "keywordNotFound",
                                    summary = "키워드 정보 없음",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "KEYWORD_NOT_FOUND",
                                              "message": "사용자의 키워드 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<KeywordResponse> updateMyKeyword(@RequestBody KeywordRequest request) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(keywordService.updateMyKeyword(memberId, request));
    }

}
