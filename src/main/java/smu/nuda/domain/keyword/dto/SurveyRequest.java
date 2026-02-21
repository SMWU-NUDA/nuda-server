package smu.nuda.domain.keyword.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import smu.nuda.domain.keyword.entity.enums.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRequest {
    @NotNull private IrritationLevel irritationLevel;
    @NotNull private ScentLevel scent;
    @NotNull private AdhesionLevel adhesion;
    @NotNull private ThicknessLevel thickness;
    @NotNull private ChangeFrequency changeFrequency;
    @NotNull private List<Long> productIds;
}
