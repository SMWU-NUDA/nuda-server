package smu.nuda.domain.keyword.entity.enums;

import lombok.Getter;

@Getter
public enum IrritationLevel {
    NONE("거의 없음"),
    SOMETIMES("가끔"),
    OFTEN("자주");

    private final String label;

    IrritationLevel(String label) {
        this.label = label;
    }
}
