package smu.nuda.domain.ingredient.dto.enums;

import lombok.Getter;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

@Getter
public enum IngredientFilterType {
    ALL(null, false),
    WARN(RiskLevel.WARN, false),
    DANGER(RiskLevel.DANGER, false),
    INTEREST(null, true),
    AVOID(null, true);

    private final RiskLevel riskLevel;
    private final boolean requiresAuth;

    IngredientFilterType(RiskLevel riskLevel, boolean requiresAuth) {
        this.riskLevel = riskLevel;
        this.requiresAuth = requiresAuth;
    }

    public boolean isRiskFilter() {
        return riskLevel != null;
    }

    public boolean isInterest() {
        return this == INTEREST;
    }

    public boolean isAvoid() {
        return this == AVOID;
    }
}
