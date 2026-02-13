package smu.nuda.domain.ingredient.dto;


public record IngredientCsvRow(
        String externalProductId,
        String categoryCode,
        String layerGroup,
        String subMaterial,
        String content,
        int rowNumber
) {}

