package smu.nuda.domain.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.member.entity.enums.SignupStepType;
import smu.nuda.domain.survey.dto.SurveyProductRequest;
import smu.nuda.domain.survey.dto.SurveyRequest;
import smu.nuda.domain.survey.service.SurveyProductService;
import smu.nuda.domain.survey.service.SurveyService;
import smu.nuda.global.guard.annotation.SignupStep;
import smu.nuda.global.guard.annotation.SignupTokenRequired;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.security.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys")
@Tag(name = "[SURVEY] 설문 API", description = "회원가입 시 설문조사 API")
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyProductService surveyProductService;

    @PostMapping
    @Operation(
            summary = "설문조사 제출(3단계)",
            description = "회원가입 과정에서 사용자가 설문조사 항목에 응답한 내용을 저장합니다. 배송지 입력 후 요청해주세요."
    )
    @SecurityRequirement(name = "JWT")
    @SignupStep(SignupStepType.DELIVERY)
    @SignupTokenRequired
    public ApiResponse<Long> submitSurvey(@RequestBody SurveyRequest request) {
        return ApiResponse.success(surveyService.submitSurvey(request));
    }

    @PostMapping("/{surveyId}/products")
    @Operation(
            summary = "설문 기반 상품 선택(3-2단계, 필수 아님)",
            description = "설문조사 문항 중 사용자가 선택한 상품 목록을 저장합니다. 여러 개의 productId를 리스트로 요청해주세요."
    )
    @SignupStep(SignupStepType.DELIVERY)
    @SignupTokenRequired
    public ApiResponse<List<Long>> addSurveyProducts(@PathVariable Long surveyId, @RequestBody SurveyProductRequest request) {
        return ApiResponse.success(surveyProductService.addSurveyProducts(surveyId, request.getProductIds()));
    }
}
