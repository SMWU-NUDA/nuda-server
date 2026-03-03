package smu.nuda.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.nuda.global.ml.dto.MlReviewKeywordsResponse;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SentimentKeywordsItem {

    private List<String> positive;
    private List<String> negative;

    public static SentimentKeywordsItem from(MlReviewKeywordsResponse response) {

        if (response == null) {
            return SentimentKeywordsItem.builder()
                    .positive(List.of())
                    .negative(List.of())
                    .build();
        }

        List<String> positiveKeywords = response.positive() != null
                        ? response.positive().stream().map(MlReviewKeywordsResponse.KeywordItem::keyword).toList()
                        : List.of();

        List<String> negativeKeywords = response.negative() != null
                        ? response.negative().stream().map(MlReviewKeywordsResponse.KeywordItem::keyword).toList()
                        : List.of();

        return SentimentKeywordsItem.builder()
                .positive(positiveKeywords)
                .negative(negativeKeywords)
                .build();
    }
}
