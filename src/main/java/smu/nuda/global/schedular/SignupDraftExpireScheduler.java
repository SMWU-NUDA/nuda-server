package smu.nuda.global.schedular;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import smu.nuda.domain.signupdraft.service.SignupDraftService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignupDraftExpireScheduler {

    private final SignupDraftService signupDraftService;

//    @Scheduled(cron = "0 0 3 * * *")
    @Scheduled(cron = "0 */1 * * * *")
    public void expireSignupDrafts() {
        int expiredCount = signupDraftService.expireDrafts();

        log.info(
                "[SignupDraftExpireJob] status=SUCCESS expiredCount={}",
                expiredCount
        );
    }
}