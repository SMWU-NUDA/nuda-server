package smu.nuda.domain.keyword.entity.enums;

import lombok.Getter;

@Getter
public enum AdhesionLevel {
    WEAK("접착력 무관"),
    NORMAL("접착력 보통"),
    STRONG("접착력 중시");

    private final String label;

    AdhesionLevel(String label) {
        this.label = label;
    }
}
