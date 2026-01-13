package smu.nuda.domain.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import smu.nuda.domain.survey.entity.enums.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRequest {
    @NotNull private IrritationLevel irritationLevel;
    @NotNull private ScentLevel scent;
    @NotNull private ChangeFrequency changeFrequency;
    @NotNull private ThicknessLevel thickness;
    @NotNull private PriorityType priority;
    @NotNull private List<Long> productIds;
}
