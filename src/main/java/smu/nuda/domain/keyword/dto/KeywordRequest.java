package smu.nuda.domain.keyword.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.keyword.entity.enums.*;

@Getter
@NoArgsConstructor
public class KeywordRequest {
    private IrritationLevel irritationLevel;
    private ScentLevel scent;
    private AdhesionLevel adhesion;
    private ThicknessLevel thickness;
}
