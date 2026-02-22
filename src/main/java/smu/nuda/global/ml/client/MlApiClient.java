package smu.nuda.global.ml.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.global.ml.dto.KeywordSyncRequest;
import smu.nuda.global.ml.dto.KeywordSyncResponse;

@Component
@RequiredArgsConstructor
public class MlApiClient {

    private final MlHttpClient httpClient;

    public KeywordSyncResponse syncKeywordPreference(KeywordSyncRequest request) {
        return httpClient.post(
                "/members/{memberId}/preference/keyword-update",
                request,
                KeywordSyncResponse.class,
                request.memberId()
        );
    }

}
