package smu.nuda.domain.signupdraft.dto;

import lombok.Builder;
import lombok.Getter;
import smu.nuda.domain.survey.entity.enums.*;

import java.util.List;

@Getter
@Builder
public class SurveyInfo {
    private IrritationLevel irritationLevel;
    private ScentLevel scent;
    private ChangeFrequency changeFrequency;
    private ThicknessLevel thickness;
    private PriorityType priority;
    private List<Long> productIds;
}
