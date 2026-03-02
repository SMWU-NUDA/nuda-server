package smu.nuda.domain.review.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewKeywordType {
    DEFAULT("default"),
    IRRITATION_LEVEL("irritationLevel"),
    SCENT("scent"),
    ABSORBENCY("absorption"),
    ADHESION("adhesion");

    private final String mlParam;
}
