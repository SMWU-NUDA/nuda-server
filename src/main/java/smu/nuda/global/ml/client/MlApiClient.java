package smu.nuda.global.ml.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.global.ml.dto.KeywordSyncRequest;
import smu.nuda.global.ml.dto.KeywordSyncResponse;
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
    public MlRankingResponse getProductGlobalRanking(String keyword, int topK) {
        return httpClient.get(
                "/ml/products/global-score?keyword={keyword}&topK={topK}",
                MlRankingResponse.class,
                keyword,
                topK
        );
    }

    // 키워드별 맞춤 상품 랭킹 조회
    public MlRankingResponse getProductPersonalRanking(Long memberId, String keyword, int topK) {
        return httpClient.get(
                "/ml/products/personalized-rank?memberId={memberId}&keyword={keyword}&topK={topK}",
                MlRankingResponse.class,
                memberId,
                keyword,
                topK
        );
    }

    // 키워드별 전체 리뷰 랭킹 조회

}
