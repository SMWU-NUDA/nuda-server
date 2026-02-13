package smu.nuda.domain.ingredient.dto;

import lombok.Builder;
import lombok.Getter;
import smu.nuda.domain.ingredient.entity.enums.LayerType;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

import java.util.Map;

@Getter
@Builder
public class IngredientSummaryResponse {
    private Long productId;
    private int totalCount;

    private Map<RiskLevel, Long> riskCounts;
    private Map<LayerType, Long> ingredientCounts;

    private MyIngredientLikeSummary myIngredientCounts;

    @Getter
    @Builder
    public static class MyIngredientLikeSummary {
        private long prefer;
        private long avoided;
    }
}
