package smu.nuda.domain.product.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductKeywordType {
    DEFAULT("default"),
    IRRITATION_LEVEL("irritationLevel"),
    SCENT("scent"),
    ABSORBENCY("absorption"),
    ADHESION("adhesion");

    private final String mlParam;
}
