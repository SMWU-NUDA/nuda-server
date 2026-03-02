package smu.nuda.global.cache.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.review.dto.enums.ReviewKeywordType;
import smu.nuda.global.cache.CacheKeyFactory;
import smu.nuda.global.cache.CachePolicy;
import smu.nuda.global.cache.CacheTemplate;
import smu.nuda.global.ml.client.MlApiClient;

import java.util.List;

@Component
@RequiredArgsConstructor
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
}
