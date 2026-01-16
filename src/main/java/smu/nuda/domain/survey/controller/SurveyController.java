package smu.nuda.domain.survey.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys")
@Tag(name = "[SURVEY] 설문 API", description = "회원가입 시 설문조사 API")
public class SurveyController {
}
