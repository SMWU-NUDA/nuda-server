package smu.nuda.domain.keyword.entity.enums;

import lombok.Getter;

@Getter
public enum ScentLevel {
    NONE("무향"),
    MILD("은은한 향"),
    STRONG("강한 향");

    private final String label;

    ScentLevel(String label) {
        this.label = label;
    }
}
