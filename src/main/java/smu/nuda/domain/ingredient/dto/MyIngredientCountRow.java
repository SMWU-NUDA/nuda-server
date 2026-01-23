package smu.nuda.domain.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyIngredientCountRow {
    private boolean preference; // true = prefer, false = avoided
    private long count;
}
