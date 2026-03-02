package smu.nuda.global.ml.dto;

public record MlReviewSentimentResponse(
        Long productId,
        SentimentDistribution sentimentDistribution,
        int totalCount,
        String analyzedAt
) {
    public record SentimentDistribution(
            int positive,
            int negative
    ) {}
}
