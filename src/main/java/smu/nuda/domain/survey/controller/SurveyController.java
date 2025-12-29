package smu.nuda.domain.survey.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.survey.dto.SurveyCreateRequest;
import smu.nuda.domain.survey.service.SurveyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public Long submitSurvey(@RequestParam Long memberId, @RequestBody SurveyCreateRequest request) {
        return surveyService.submitSurvey(memberId, request);
    }
}
