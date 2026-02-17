package smu.nuda.domain.keyword.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.keyword.dto.KeywordResponse;
import smu.nuda.domain.keyword.service.KeywordService;
import smu.nuda.domain.member.entity.Member;
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
    public ApiResponse<KeywordResponse> getDelivery() {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(keywordService.getMyKeyword(member));
    }

}
