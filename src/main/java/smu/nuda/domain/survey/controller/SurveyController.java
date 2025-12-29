package smu.nuda.domain.survey.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.survey.dto.SurveyProductRequest;
import smu.nuda.domain.survey.dto.SurveyRequest;
import smu.nuda.domain.survey.service.SurveyProductService;
import smu.nuda.domain.survey.service.SurveyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys")
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyProductService surveyProductService;

    @PostMapping
    public Long submitSurvey(@RequestParam Long memberId, @RequestBody SurveyRequest request) {
        return surveyService.submitSurvey(memberId, request);
    }

    @PostMapping("/{surveyId}/products")
    public void addSurveyProducts(@PathVariable Long surveyId, @RequestBody SurveyProductRequest request) {
        surveyProductService.addSurveyProducts(surveyId, request.getProductIds()
        );
    }
}
