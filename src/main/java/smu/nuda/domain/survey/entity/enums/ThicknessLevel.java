package smu.nuda.domain.survey.entity.enums;

import lombok.Getter;

@Getter
public enum ThicknessLevel {
    THIN("얇음"),
    NORMAL("일반"),
    THICK("도톰함");

    private final String label;

    ThicknessLevel(String label) {
        this.label = label;
    }
}

