package smu.nuda.global.ml.dto;

import java.util.List;

public record MlReviewKeywordsResponse(
        Long productId,
        List<KeywordItem> positive,
        List<KeywordItem> negative,
        int totalCount,
        String analyzedAt
) {
    public record KeywordItem(String keyword) {}
}
