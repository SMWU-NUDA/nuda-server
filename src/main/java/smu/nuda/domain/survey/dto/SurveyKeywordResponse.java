package smu.nuda.domain.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.survey.entity.Survey;

import java.util.List;

@Getter
@AllArgsConstructor
public class SurveyKeywordResponse {

    private List<String> keywords;

    public static SurveyKeywordResponse from(Survey survey) {
        return new SurveyKeywordResponse(
                List.of(
                        survey.getIrritationLevel().getLabel(),
                        survey.getScent().getLabel(),
                        survey.getChangeFrequency().getLabel(),
                        survey.getThickness().getLabel(),
                        survey.getPriority().getLabel()
                )
        );
    }
}
