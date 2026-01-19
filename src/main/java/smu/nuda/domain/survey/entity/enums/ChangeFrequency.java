package smu.nuda.domain.survey.entity.enums;

import lombok.Getter;

@Getter
public enum ChangeFrequency {

    LOW("2~3회 이하"),
    MEDIUM("4~5회"),
    HIGH("6회 이상");

    private final String label;

    ChangeFrequency(String label) {
        this.label = label;
    }
}

