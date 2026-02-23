package smu.nuda.domain.keyword.entity.enums;

import lombok.Getter;

@Getter
public enum IrritationLevel {
    NONE("민감도 낮음"),
    SOMETIMES("민감도 보통"),
    OFTEN("민감도 높음");

    private final String label;

    IrritationLevel(String label) {
        this.label = label;
    }
}
