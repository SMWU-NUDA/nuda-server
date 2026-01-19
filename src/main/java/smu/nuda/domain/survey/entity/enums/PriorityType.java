package smu.nuda.domain.survey.entity.enums;

import lombok.Getter;

@Getter
public enum PriorityType {
    SAFETY("안전한 성분"),
    ABSORPTION("흡수력"),
    SOFTNESS("부드러움");

    private final String label;

    PriorityType(String label) {
        this.label = label;
    }
}

