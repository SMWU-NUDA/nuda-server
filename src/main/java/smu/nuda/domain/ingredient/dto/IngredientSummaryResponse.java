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

    private Map<RiskLevel, Long> globalRiskCounts;
    private Map<LayerType, LayerSummary> ingredientCounts;

    private MyIngredientLikeSummary myIngredientCounts;

    @Getter
    public static class LayerSummary {
        private long count;
        private Map<RiskLevel, Long> riskCounts;

        public LayerSummary(Map<RiskLevel, Long> riskCounts) {
            this.riskCounts = riskCounts;
        }

        public void increaseCount(long value) {
            this.count += value;
        }
    }

    @Getter
    @Builder
    public static class MyIngredientLikeSummary {
        private long prefer;
        private long avoided;
    }
}
