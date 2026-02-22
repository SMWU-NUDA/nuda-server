package smu.nuda.domain.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.ingredient.entity.enums.LayerType;

import java.util.List;

@Getter
@AllArgsConstructor
public class IngredientDetailResponse {
    private Long ingredientId;
    private String name;
    private LayerType layerType;
    private String description;
    private Boolean preference; // true: 관심 | false: 피하기 | null: 즐겨찾기 안함
    private String caution; // extracted.ui_notice
    private List<HazardItem> hCodes; // extracted.hazard_statements
}
