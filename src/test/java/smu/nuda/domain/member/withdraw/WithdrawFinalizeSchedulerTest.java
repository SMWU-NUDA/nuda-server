package smu.nuda.domain.member.withdraw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Role;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.withdraw.scheduler.WithdrawFinalizeScheduler;
import smu.nuda.domain.member.repository.MemberRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("WithdrawFinalizeScheduler 통합 테스트")
class WithdrawFinalizeSchedulerTest {

    /*
    탈퇴 확정 스케줄러가 30일 경과한 WITHDRAW_REQUESTED 멤버만 WITHDRAWN으로 전이하는지 검증함

    - deletedAt이 30일을 초과한 멤버 → WITHDRAWN
    - deletedAt이 30일 미만인 멤버 → 변화 없음
    - ACTIVE 멤버 → 변화 없음
     */

    @Autowired
    WithdrawFinalizeScheduler scheduler;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("deletedAt이 31일 전인 WITHDRAW_REQUESTED 멤버는 스케줄러 실행 후 WITHDRAWN이 된다")
    void finalize_withdraws_expired_member() {
        // [given] 31일 전에 탈퇴 요청한 멤버
        Member member = memberRepository.saveAndFlush(
                Member.builder()
                        .username("expired_user")
                        .email("expired@test.com")
                        .password("password")
                        .role(Role.USER)
                        .status(Status.WITHDRAW_REQUESTED)
                        .deletedAt(LocalDateTime.now().minusDays(31))
                        .build()
        );

        // [when] 스케줄러 실행
        scheduler.finalizeWithdrawnMembers();

        // [then] WITHDRAWN으로 전이됨
        Member result = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(Status.WITHDRAWN);
    }

    @Test
    @DisplayName("deletedAt이 29일 전인 WITHDRAW_REQUESTED 멤버는 스케줄러 실행 후 변화 없다")
    void finalize_does_not_withdraw_member_within_window() {
        // [given] 29일 전에 탈퇴 요청한 멤버 (유예 기간 내)
        Member member = memberRepository.saveAndFlush(
                Member.builder()
                        .username("recent_user")
                        .email("recent@test.com")
                        .password("password")
                        .role(Role.USER)
                        .status(Status.WITHDRAW_REQUESTED)
                        .deletedAt(LocalDateTime.now().minusDays(29))
                        .build()
        );

        // [when] 스케줄러 실행
        scheduler.finalizeWithdrawnMembers();

        // [then] 여전히 WITHDRAW_REQUESTED 상태
        Member result = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(Status.WITHDRAW_REQUESTED);
    }

    @Test
    @DisplayName("ACTIVE 멤버는 스케줄러 실행 후 변화 없다")
    void finalize_does_not_affect_active_member() {
        // [given] ACTIVE 상태의 멤버
        Member member = memberRepository.saveAndFlush(
                Member.builder()
                        .username("active_user")
                        .email("active@test.com")
                        .password("password")
                        .role(Role.USER)
                        .status(Status.ACTIVE)
                        .build()
        );

        // [when] 스케줄러 실행
        scheduler.finalizeWithdrawnMembers();

        // [then] 여전히 ACTIVE 상태
        Member result = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
    }
}
