package smu.nuda.domain.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class IngredientResponse {
    private Long totalCount;
    private List<IngredientItem> ingredients;
}
