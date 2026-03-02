package smu.nuda.global.ml.dto;

import java.util.List;

public record MlRankingResponse(
        String keyword,
        List<Integer> rankedIds
) {}
