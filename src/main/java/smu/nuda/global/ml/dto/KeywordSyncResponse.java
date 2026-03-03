package smu.nuda.global.ml.dto;

public record KeywordSyncResponse(
        Long memberId,
        SavedPreference saved,
        String asOf
) {
    public record SavedPreference(
            int pSensitivityLevel,
            int pScentLevel,
            int pAbsorbencyLevel,
            int pAdhesionLevel
    ) {}
}
