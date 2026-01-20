package smu.nuda.domain.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

import java.util.Map;

@Getter
@AllArgsConstructor
public class IngredientSummaryResponse {
    private Long productId;
    private int totalCount;
    private Map<RiskLevel, Integer> riskCounts;
    private Map<String, Integer> componentCounts;
    private MyIngredientSummary myIngredient;
}
