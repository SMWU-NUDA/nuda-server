package smu.nuda.global.cache.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.global.cache.CacheKeyFactory;
import smu.nuda.global.cache.CachePolicy;
import smu.nuda.global.cache.CacheTemplate;
import smu.nuda.global.ml.client.MlApiClient;
import smu.nuda.global.ml.dto.MlRankingResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MlRankingCacheFacade {

    private final CacheTemplate cacheTemplate;
    private final MlApiClient mlApiClient;

    public List<Integer> getRanking(String keyword, int topK) {
        String key = CacheKeyFactory.globalRanking(keyword, topK);

        return cacheTemplate.get(
                key,
                CachePolicy.ML_GLOBAL_RANKING_TTL,
                () -> mlApiClient
                        .getKeywordRanking(keyword, topK)
                        .rankedIds(),
                    List.class
        );
    }
}
