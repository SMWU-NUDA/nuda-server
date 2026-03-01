package smu.nuda.global.ml.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.global.ml.dto.KeywordSyncRequest;
import smu.nuda.global.ml.dto.KeywordSyncResponse;
import smu.nuda.global.ml.dto.MlRankingRequest;
import smu.nuda.global.ml.dto.MlRankingResponse;

@Component
@RequiredArgsConstructor
public class MlApiClient {

    private final MlHttpClient httpClient;

    // 키워드 점수 동기화
    public KeywordSyncResponse syncKeywordPreference(KeywordSyncRequest request) {
        return httpClient.post(
                "/members/{memberId}/preference/keyword-update",
                request,
                KeywordSyncResponse.class,
                request.memberId()
        );
    }

    // 키워드별 전체 상품 랭킹 조회
    @SuppressWarnings("unchecked")
    public MlRankingResponse getKeywordRanking(String keyword, int topK) {
        return httpClient.get(
                "/ml/products/global-score?keyword={keyword}&topK={topK}",
                MlRankingResponse.class,
                keyword,
                topK
        );
    }

}
