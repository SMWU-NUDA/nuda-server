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

    private static final int ML_TOP_K = 50;

    private final CacheTemplate cacheTemplate;
    private final MlApiClient mlApiClient;

    public List<Integer> getRanking(ProductKeywordType keyword) {
        String key = CacheKeyFactory.globalRanking(keyword, ML_TOP_K);

        return cacheTemplate.get(
                key,
                CachePolicy.ML_GLOBAL_RANKING_TTL,
                () -> mlApiClient
                        .getKeywordRanking(keyword.getMlParam(), ML_TOP_K)
                        .rankedIds(),
                    List.class
        );
    }
}
