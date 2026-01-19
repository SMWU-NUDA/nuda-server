package smu.nuda.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.survey.dto.SurveyKeywordResponse;

@Getter
@AllArgsConstructor
public class MyPageResponse {
    private MeResponse me;
    private SurveyKeywordResponse survey;
}
