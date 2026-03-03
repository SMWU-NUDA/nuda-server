package smu.nuda.global.ml.dto;

import java.util.List;

public record MlReviewTrendResponse(
        Long productId,
        int totalReviewCount,
        List<String> trendHighlights,
        String analyzedAt
) {}
