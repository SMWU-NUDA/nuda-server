package smu.nuda.global.cache.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import smu.nuda.domain.review.dto.enums.ReviewKeywordType;
import smu.nuda.global.cache.CacheKeyFactory;
import smu.nuda.global.cache.CachePolicy;
import smu.nuda.global.cache.CacheTemplate;
import smu.nuda.global.ml.client.MlApiClient;
import smu.nuda.global.ml.dto.MlReviewKeywordsResponse;
import smu.nuda.global.ml.dto.MlReviewSentimentResponse;
import smu.nuda.global.ml.dto.MlReviewTrendResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MlReviewCacheFacade {

    private static final int GLOBAL_TOP_K = 300;

    private final CacheTemplate cacheTemplate;
    private final CacheKeyFactory cacheKeyFactory;
    private final MlApiClient mlApiClient;

    public List<Integer> getGlobalReviewRanking(Long productId, ReviewKeywordType keyword) {
        String key = cacheKeyFactory.reviewGlobalRanking(productId, keyword, GLOBAL_TOP_K);

        return cacheTemplate.get(
                key,
                CachePolicy.ML_REVIEW_GLOBAL_RANKING_TTL,
                () -> mlApiClient
                        .getReviewGlobalRanking(productId, keyword.getMlParam(), GLOBAL_TOP_K)
                        .rankedIds(),
                List.class
        );
    }

    public MlReviewKeywordsResponse getReviewKeywords(Long productId, int topN) {
        String key = cacheKeyFactory.reviewKeywords(productId, topN);

        return cacheTemplate.get(
                key,
                CachePolicy.ML_REVIEW_KEYWORDS_TTL,
                () -> {
                    long start = System.currentTimeMillis();
                    MlReviewKeywordsResponse result = mlApiClient.getReviewKeywords(productId, topN);
                    long end = System.currentTimeMillis();
                    log.info("ML Keyword API Time = {} ms", (end - start));
                    return result;
                },
                MlReviewKeywordsResponse.class
        );
    }

    public MlReviewTrendResponse getReviewTrend(Long productId) {
        String key = cacheKeyFactory.reviewTrend(productId);

        return cacheTemplate.get(
                key,
                CachePolicy.ML_REVIEW_TREND_TTL,
                () -> {
                    long start = System.currentTimeMillis();
                    MlReviewTrendResponse result = mlApiClient.getReviewTrend(productId);
                    long end = System.currentTimeMillis();
                    log.info("ML Trend API Time = {} ms", (end - start));
                    return result;
                },
                MlReviewTrendResponse.class
        );
    }

    public MlReviewSentimentResponse getReviewSentiment(Long productId) {
        String key = cacheKeyFactory.reviewSentiment(productId);

        return cacheTemplate.get(
                key,
                CachePolicy.ML_REVIEW_SENTIMENT_TTL,
                () -> {
                    long start = System.currentTimeMillis();
                    MlReviewSentimentResponse result = mlApiClient.getReviewSentiment(productId);
                    long end = System.currentTimeMillis();
                    log.info("ML Sentiment API Time = {} ms", (end - start));
                    return result;
                },
                MlReviewSentimentResponse.class
        );
    }
}
