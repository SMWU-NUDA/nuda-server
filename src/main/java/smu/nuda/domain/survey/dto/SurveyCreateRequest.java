package smu.nuda.domain.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.survey.entity.enums.*;

@Getter
@NoArgsConstructor
public class SurveyCreateRequest {
    private IrritationLevel irritationLevel;
    private ScentLevel scent;
    private ChangeFrequency changeFrequency;
    private ThicknessLevel thickness;
    private PriorityType priority;
}
