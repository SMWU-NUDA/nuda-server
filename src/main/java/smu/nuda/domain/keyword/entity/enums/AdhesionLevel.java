package smu.nuda.domain.keyword.entity.enums;

import lombok.Getter;

@Getter
public enum AdhesionLevel {
    WEAK("약함"),
    NORMAL("보통"),
    STRONG("강함");

    private final String label;

    AdhesionLevel(String label) {
        this.label = label;
    }
}
