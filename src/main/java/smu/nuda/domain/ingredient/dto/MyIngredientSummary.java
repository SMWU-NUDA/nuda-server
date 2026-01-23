package smu.nuda.domain.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyIngredientSummary {
    private int prefer;
    private int avoided;
}
