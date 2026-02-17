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
    private ChangeFrequency changeFrequency;
    private ThicknessLevel thickness;
    private PriorityType priority;

    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(
                keyword.getIrritationLevel(),
                keyword.getScent(),
                keyword.getChangeFrequency(),
                keyword.getThickness(),
                keyword.getPriority()
        );
    }
}
