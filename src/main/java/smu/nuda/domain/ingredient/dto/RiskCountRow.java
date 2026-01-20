package smu.nuda.domain.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

@Getter
@AllArgsConstructor
public class RiskCountRow {
    private RiskLevel riskLevel;
    private long count;
}
