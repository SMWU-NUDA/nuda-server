package smu.nuda.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.nuda.global.ml.dto.MlReviewKeywordsResponse;
import smu.nuda.global.ml.dto.MlReviewSentimentResponse;
import smu.nuda.global.ml.dto.MlReviewTrendResponse;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReviewAiSummaryResponse {
    private SentimentKeywordsItem keywords;
    private Integer satisfactionRate;
    private List<String> trendHighlights;

    public static ReviewAiSummaryResponse of(
            MlReviewTrendResponse trend,
            MlReviewSentimentResponse sentiment,
            MlReviewKeywordsResponse keywords
    ) {
        int positive = sentiment.sentimentDistribution().positive();
        int negative = sentiment.sentimentDistribution().negative();

        int total = positive + negative;
        int satisfactionRate = total == 0 ? 0 : (int) Math.round((positive * 100.0) / total);

        return ReviewAiSummaryResponse.builder()
                .keywords(SentimentKeywordsItem.from(keywords))
                .satisfactionRate(satisfactionRate)
                .trendHighlights(trend.trendHighlights())
                .build();
    }
}
