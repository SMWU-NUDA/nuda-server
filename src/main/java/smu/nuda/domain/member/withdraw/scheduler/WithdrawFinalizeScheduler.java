package smu.nuda.domain.member.withdraw.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.domain.member.withdraw.event.WithdrawCompletedEvent;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WithdrawFinalizeScheduler {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void finalizeWithdrawnMembers() {
        LocalDateTime threshold = LocalDateTime.now(clock).minusDays(30);
        List<Member> expiredMembers = memberRepository
                .findAllByStatusAndDeletedAtBefore(Status.WITHDRAW_REQUESTED, threshold);

        for (Member member : expiredMembers) {
            member.withdraw();
            eventPublisher.publishEvent(new WithdrawCompletedEvent(member.getId()));
        }
    }
}