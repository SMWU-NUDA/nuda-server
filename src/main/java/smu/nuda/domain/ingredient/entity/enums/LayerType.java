package smu.nuda.domain.ingredient.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LayerType {
    TOP_SHEET("표지"),
    ABSORBER("흡수체"),
    BACK_SHEET("방수층"),
    ADHESIVE("접착제"),
    ADDITIVE("기타");

    private final String description;
}
