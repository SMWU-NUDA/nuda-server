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
        double positiveRatio = sentiment.sentimentDistribution().positive();
        int satisfactionRate = (int) Math.round(positiveRatio * 100);

        return ReviewAiSummaryResponse.builder()
                .keywords(SentimentKeywordsItem.from(keywords))
                .satisfactionRate(satisfactionRate)
                .trendHighlights(trend.trendHighlights())
                .build();
    }
}
