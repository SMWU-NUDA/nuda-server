package smu.nuda.domain.signupdraft.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.signupdraft.dto.SignupDraftCreateResponse;
import smu.nuda.domain.signupdraft.usecase.SignupDraftUseCase;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
@Tag(name = "[SIGNUP] 회원가입 API", description = "회원가입 유스케이스 관련 API")
public class SignupDraftController {

    private final SignupDraftUseCase signupDraftUseCase;

    @PostMapping("/draft")
    @Operation(
            summary = "회원가입 임시 저장 생성",
            description = "회원가입 과정에서 사용할 임시 저장(Draft) 데이터를 생성합니다. " +
                    "생성된 Draft ID를 통해 단계별로 회원가입 정보를 저장 및 조회 할 수 있습니다."
    )
    public ApiResponse<SignupDraftCreateResponse> createDraft() {
        return ApiResponse.success(signupDraftUseCase.createDraft());
    }

    @GetMapping("/draft")
    @Operation(
            summary = "회원가입 임시 저장 조회",
            description = "Signup-Token을 통해 저장된 회원가입 임시 데이터를 조회합니다. "
    )
    public ApiResponse<SignupDraftCreateResponse> getDraft(@RequestHeader("Signup-Token") String signupToken) {
        return ApiResponse.success(signupDraftUseCase.getDraft(signupToken));
    }

}
