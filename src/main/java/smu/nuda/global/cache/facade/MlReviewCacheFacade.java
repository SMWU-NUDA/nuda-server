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

        return cacheTemplate.getWithFallback(
                key,
                CachePolicy.ML_REVIEW_KEYWORDS_TTL,
                () -> mlApiClient.getReviewKeywords(productId, topN),
                () -> defaultKeywords(productId, topN),
                MlReviewKeywordsResponse.class
        );
    }

    public MlReviewTrendResponse getReviewTrend(Long productId) {
        String key = cacheKeyFactory.reviewTrend(productId);

        return cacheTemplate.getWithFallback(
                key,
                CachePolicy.ML_REVIEW_TREND_TTL,
                () -> mlApiClient.getReviewTrend(productId),
                () -> defaultTrend(productId),
                MlReviewTrendResponse.class
        );
    }

    public MlReviewSentimentResponse getReviewSentiment(Long productId) {
        String key = cacheKeyFactory.reviewSentiment(productId);

        return cacheTemplate.getWithFallback(
                key,
                CachePolicy.ML_REVIEW_SENTIMENT_TTL,
                () -> mlApiClient.getReviewSentiment(productId),
                () -> defaultSentiment(productId),
                MlReviewSentimentResponse.class
        );
    }

    private MlReviewKeywordsResponse defaultKeywords(Long productId, int topN) {
        return new MlReviewKeywordsResponse(
                productId,
                List.of(),
                List.of(),
                0,
                null
        );
    }

    private MlReviewTrendResponse defaultTrend(Long productId) {
        return new MlReviewTrendResponse(
                productId,
                0,
                List.of(),
                null
        );
    }

    private MlReviewSentimentResponse defaultSentiment(Long productId) {
        return new MlReviewSentimentResponse(
                productId,
                new MlReviewSentimentResponse.SentimentDistribution(0.5, 0.5),
                0,
                null
        );
    }
}
