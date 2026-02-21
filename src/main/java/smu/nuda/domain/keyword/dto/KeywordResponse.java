package smu.nuda.domain.keyword.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.keyword.entity.Keyword;
import smu.nuda.domain.keyword.entity.enums.*;

@Getter
@AllArgsConstructor
public class KeywordResponse {

    private IrritationLevel irritationLevel;
    private ScentLevel scent;
    private AdhesionLevel adhesion;
    private ThicknessLevel thickness;

    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(
                keyword.getIrritationLevel(),
                keyword.getScent(),
                keyword.getAdhesion(),
                keyword.getThickness()
        );
    }
}
