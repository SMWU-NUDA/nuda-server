package smu.nuda.global.ml.dto;

import java.time.Instant;

public record KeywordSyncResponse(
        Long memberId,
        SavedPreference saved,
        Instant asOf
) {
    public record SavedPreference(
            int pSensitivityLevel,
            int pScentLevel,
            int pAbsorbencyLevel,
            int pAdhesionLevel,
            int pSafetyLevel
    ) {}
}
