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
                mapIrritation(irritation),
                mapScent(scent),
                mapAbsorption(absorption),
                mapAdhesion(adhesion)
        );
    }

    private static String mapAdhesion(Enum<?> level) {
        return switch (level.name()) {
            case "WEAK" -> "LOW";
            case "NORMAL" -> "MEDIUM";
            case "STRONG" -> "HIGH";
            default -> throw new IllegalArgumentException("Unknown level: " + level);
        };
    }

    private static String mapAbsorption(Enum<?> level) {
        return switch (level.name()) {
            case "THIN" -> "LOW";
            case "NORMAL" -> "MEDIUM";
            case "THICK" -> "HIGH";
            default -> throw new IllegalArgumentException("Unknown level: " + level);
        };
    }

    private static String mapIrritation(IrritationLevel level) {
        return level.name();
    }

    private static String mapScent(ScentLevel level) {
        return level.name();
    }
}
