package smu.nuda.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import smu.nuda.domain.signupdraft.service.SignupDraftService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignupDraftExpireScheduler {

    private final SignupDraftService signupDraftService;

    @Scheduled(cron = "0 0 3 * * *")
    public void expireSignupDrafts() {
        LocalDateTime startedAt = LocalDateTime.now();
        long startMillis = System.currentTimeMillis();

        try {
            int expiredCount = signupDraftService.expireDrafts();
            long duration = System.currentTimeMillis() - startMillis;

            log.info(
                    "[SignupDraftExpireJob] status=SUCCESS expiredCount={} durationMs={} executedAt={}",
                    expiredCount,
                    duration,
                    startedAt
            );
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startMillis;

            log.error(
                    "[SignupDraftExpireJob] status=FAIL durationMs={} executedAt={} message={}",
                    duration,
                    startedAt,
                    e.getMessage(),
                    e
            );
        }
    }
}