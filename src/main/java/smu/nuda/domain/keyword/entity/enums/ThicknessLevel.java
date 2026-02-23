package smu.nuda.domain.keyword.entity.enums;

import lombok.Getter;

@Getter
public enum ThicknessLevel {
    THIN("약한 흡수력"),
    NORMAL("보통 흡수력"),
    THICK("높은 흡수력");

    private final String label;

    ThicknessLevel(String label) {
        this.label = label;
    }
}

