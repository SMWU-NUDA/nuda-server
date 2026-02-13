package smu.nuda.domain.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.ingredient.entity.enums.LayerType;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

@Getter
@AllArgsConstructor
public class IngredientItem {
    private Long ingredientId;
    private String name;
    private RiskLevel riskLevel;
    private LayerType layerType;

}
