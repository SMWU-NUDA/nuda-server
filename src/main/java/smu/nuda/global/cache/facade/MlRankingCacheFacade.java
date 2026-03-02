package smu.nuda.global.cache.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.product.dto.enums.ProductKeywordType;
import smu.nuda.global.cache.CacheKeyFactory;
import smu.nuda.global.cache.CachePolicy;
import smu.nuda.global.cache.CacheTemplate;
import smu.nuda.global.ml.client.MlApiClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MlRankingCacheFacade {

    private static final int GLOBAL_TOP_K = 50;
    private static final int PERSONAL_TOP_K = 500;

    private final CacheTemplate cacheTemplate;
    private final CacheKeyFactory cacheKeyFactory;
    private final MlApiClient mlApiClient;

    public List<Integer> getGlobalRanking(ProductKeywordType keyword) {
        String key = cacheKeyFactory.globalRanking(keyword, GLOBAL_TOP_K);

        return cacheTemplate.get(
                key,
                CachePolicy.ML_GLOBAL_RANKING_TTL,
                () -> mlApiClient
                        .getKeywordRanking(keyword.getMlParam(), GLOBAL_TOP_K)
                        .rankedIds(),
                    List.class
        );
    }

    public List<Integer> getPersonalRanking(Long memberId, ProductKeywordType keyword) {
        String key = cacheKeyFactory.personalRanking(memberId, keyword, PERSONAL_TOP_K);

        return cacheTemplate.get(
                key,
                CachePolicy.ML_PERSONAL_RANKING_TTL,
                () -> mlApiClient
                        .getPersonalRanking(memberId, keyword.getMlParam(), PERSONAL_TOP_K)
                        .rankedIds(),
                List.class
        );
    }
}
