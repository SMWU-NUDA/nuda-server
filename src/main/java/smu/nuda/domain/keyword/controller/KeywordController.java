package smu.nuda.domain.keyword.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.keyword.dto.KeywordRequest;
import smu.nuda.domain.keyword.dto.KeywordResponse;
import smu.nuda.domain.keyword.service.KeywordService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "[KEYWORD] 키워드 API", description = "사용자의 키워드 API")
public class KeywordController {

    private final KeywordService keywordService;
    private final AuthenticationGuard authenticationGuard;

    @GetMapping("/me/keywords")
    @Operation(
            summary = "키워드 조회",
            description = "회원가입시 설문조사로 선택한 키워드를 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<KeywordResponse> getMyKeyword() {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(keywordService.getMyKeyword(memberId));
    }

    @PatchMapping("/me/keywords")
    @Operation(
            summary = "키워드 수정",
            description = "사용자의 키워드를 업데이트합니다. 기존 키워드가 존재하지 않을 경우 에러를 반환합니다." +
                    "모든 필드를 보낼 필요 없이, 변경을 원하는 항목만 선택적으로 포함하여 수정이 가능합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<KeywordResponse> updateMyKeyword(@RequestBody KeywordRequest request) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(keywordService.updateMyKeyword(memberId, request));
    }

}
