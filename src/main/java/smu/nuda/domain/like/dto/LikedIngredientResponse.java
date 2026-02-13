package smu.nuda.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedIngredientResponse {
    private Boolean preference;

    public static LikedIngredientResponse interested() {
        return new LikedIngredientResponse(true);
    }

    public static LikedIngredientResponse avoided() {
        return new LikedIngredientResponse(false);
    }

    public static LikedIngredientResponse none() {
        return new LikedIngredientResponse(null);
    }
}
