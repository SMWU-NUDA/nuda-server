package smu.nuda.domain.signupdraft.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.member.dto.DeliveryRequest;
import smu.nuda.domain.signupdraft.dto.AccountRequest;
import smu.nuda.domain.signupdraft.dto.SignupDraftDetailResponse;
import smu.nuda.domain.signupdraft.dto.SignupDraftResponse;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.domain.signupdraft.guard.annotation.SignupDraftStep;
import smu.nuda.domain.signupdraft.usecase.SignupDraftUseCase;
import smu.nuda.domain.keyword.dto.KeywordRequest;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
@Tag(name = "[SIGNUP] 회원가입 API", description = "회원가입 유스케이스 관련 API")
public class SignupDraftController {

    private final SignupDraftUseCase signupDraftUseCase;

    @PostMapping("/draft")
    @Operation(
            summary = "임시 저장 생성(1단계)",
            description = "회원가입 과정에서 사용할 임시 저장(Draft) 데이터를 생성합니다. " +
                    "생성된 Draft ID를 통해 단계별로 회원가입 정보를 저장 및 조회 할 수 있습니다."
    )
    public ApiResponse<SignupDraftResponse> createDraft() {
        return ApiResponse.success(signupDraftUseCase.createDraft());
    }

    @GetMapping("/draft")
    @Operation(
            summary = "회원가입 임시 저장 조회",
            description = "Signup-Token을 통해 저장된 회원가입 임시 데이터를 조회합니다."
    )
    public ApiResponse<SignupDraftDetailResponse> getDraft(@RequestHeader("Signup-Token") String signupToken) {
        return ApiResponse.success(signupDraftUseCase.getDraft(signupToken));
    }

    @PutMapping("/draft/account")
    @Operation(
            summary = "계정 정보(2단계)",
            description = "이메일, 사용자명, 비밀번호, 닉네임 등 기본 계정 정보를 저장합니다. 마지막 요청값으로 항상 덮어씁니다."
    )
    @SignupDraftStep(SignupStep.ACCOUNT)
    public ApiResponse<String> updateAccount(@RequestHeader("Signup-Token") String signupToken, @RequestBody @Valid AccountRequest request) {
        signupDraftUseCase.updateAccount(signupToken, request);
        return ApiResponse.success("기본 정보 입력이 완료되었습니다. 배송지입력을 진행해주세요.");
    }

    @PutMapping("/draft/delivery")
    @Operation(
            summary = "배송지 입력(3단계)",
            description = "회원가입 과정에서 사용할 기본 배송지 정보(주소, 수령인 등)를 저장합니다."
    )
    @SignupDraftStep(SignupStep.DELIVERY)
    public ApiResponse<String> enterDelivery(@RequestHeader("Signup-Token") String signupToken, @RequestBody @Valid DeliveryRequest request) {
        signupDraftUseCase.updateDelivery(signupToken, request);
        return ApiResponse.success("배송지 입력이 완료되었습니다. 설문조사를 진행해주세요.");
    }

    @PutMapping("/draft/survey")
    @Operation(
            summary = "설문 조사(4단계)",
            description = "회원가입 과정에서 사용할 설문 정보(상품 선택 필수)를 저장합니다."
    )
    @SignupDraftStep(SignupStep.SURVEY)
    public ApiResponse<String> enterKeyword(@RequestHeader("Signup-Token") String signupToken, @RequestBody @Valid KeywordRequest request) {
        signupDraftUseCase.updateKeyword(signupToken, request);
        return ApiResponse.success("설문 조사 입력이 완료되었습니다. 최종 완료를 진행해주세요.");
    }

    @PostMapping("/commit")
    @Operation(
            summary = "회원가입 완료(5단계)",
            description = "임시 저장된 가입 초안(Draft)을 확정하여 관련 엔티티를 생성합니다."
    )
    @SignupDraftStep(SignupStep.COMPLETED)
    public ApiResponse<String> commitSignup(@RequestHeader("Signup-Token") String signupToken) {
        signupDraftUseCase.commit(signupToken);
        return ApiResponse.success("회원가입이 완료되었습니다. 로그인 화면으로 이동해주세요.");
    }

}
