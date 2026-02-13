package smu.nuda.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.ingredient.entity.enums.LayerType;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

@Getter
@AllArgsConstructor
public class LikedIngredientResponse {
    private Long likeId;
    private Long ingredientId;
    private String name;
    private RiskLevel riskLevel;
    private LayerType layerType;
}
