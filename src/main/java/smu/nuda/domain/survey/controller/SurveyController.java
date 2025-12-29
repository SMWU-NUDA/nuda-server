package smu.nuda.domain.survey.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.survey.dto.SurveyProductRequest;
import smu.nuda.domain.survey.dto.SurveyRequest;
import smu.nuda.domain.survey.service.SurveyProductService;
import smu.nuda.domain.survey.service.SurveyService;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.security.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys")
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyProductService surveyProductService;

    @PostMapping
    @SecurityRequirement(name = "JWT")
    public ApiResponse<Long> submitSurvey(@RequestBody SurveyRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(surveyService.submitSurvey(request, userDetails.getMember()));
    }

    @PostMapping("/{surveyId}/products")
    public ApiResponse<List<Long>> addSurveyProducts(@PathVariable Long surveyId, @RequestBody SurveyProductRequest request) {
        return ApiResponse.success(surveyProductService.addSurveyProducts(surveyId, request.getProductIds()));
    }
}
