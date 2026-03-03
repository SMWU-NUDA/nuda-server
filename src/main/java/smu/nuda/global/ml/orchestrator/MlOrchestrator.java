package smu.nuda.global.ml.orchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import smu.nuda.global.ml.client.MlApiClient;
import smu.nuda.global.ml.dto.KeywordSyncMapper;
import smu.nuda.global.ml.dto.KeywordSyncRequest;
import smu.nuda.global.ml.dto.KeywordSyncResponse;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class MlOrchestrator {

    private final MlApiClient mlApiClient;
    private final Clock clock;

    public void handleKeywordChanged(KeywordSyncRequest request) {
        Instant start = Instant.now(clock);
        log.info("[ML-Orchestrator] Start keyword sync - memberId={}", request.memberId());

        try {
            KeywordSyncMapper keywordSyncMapper = KeywordSyncMapper.from(
                    request.memberId(),
                    request.irritationLevel(),
                    request.scent(),
                    request.absorption(),
                    request.adhesion()
            );
            KeywordSyncResponse response = mlApiClient.syncKeywordPreference(keywordSyncMapper);

            Instant end = Instant.now(clock);
            long duration = Duration.between(start, end).toMillis();

            log.info("""
                [ML-Orchestrator] Success keyword sync
                memberId={}
                duration={}ms
                saved={}
                asOf={}
                """,
                    response.memberId(),
                    duration,
                    response.saved(),
                    response.asOf()
            );
        } catch (Exception e) {
            Instant end = Instant.now(clock);
            long duration = Duration.between(start, end).toMillis();

            log.error("[ML-Orchestrator] Failed keyword sync - memberId={}, duration={}ms",
                    request.memberId(),
                    duration,
                    e);
        }
    }
}
