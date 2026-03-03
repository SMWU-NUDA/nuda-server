package smu.nuda.global.ml.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.keyword.entity.enums.AdhesionLevel;
import smu.nuda.domain.keyword.entity.enums.IrritationLevel;
import smu.nuda.domain.keyword.entity.enums.ScentLevel;
import smu.nuda.domain.keyword.entity.enums.ThicknessLevel;

@Getter
@AllArgsConstructor
public class KeywordSyncMapper {
    private Long memberId;
    private String irritationLevel;
    private String scent;
    private String absorption;
    private String adhesion;

    public static KeywordSyncMapper from(
            Long memberId,
            IrritationLevel irritation,
            ScentLevel scent,
            ThicknessLevel absorption,
            AdhesionLevel adhesion
    ) {
        return new KeywordSyncMapper(
                memberId,
                irritation.name(),
                scent.name(),
                mapAbsorption(absorption),
                mapAdhesion(adhesion)
        );
    }

    private static String mapAdhesion(AdhesionLevel level) {
        return switch (level) {
            case WEAK -> "LOW";
            case NORMAL -> "MEDIUM";
            case STRONG -> "HIGH";
            default -> throw new IllegalArgumentException("Unknown level: " + level);
        };
    }

    private static String mapAbsorption(ThicknessLevel level) {
        return switch (level) {
            case THIN -> "LOW";
            case NORMAL -> "MEDIUM";
            case THICK -> "HIGH";
            default -> throw new IllegalArgumentException("Unknown level: " + level);
        };
    }

}
