package smu.nuda.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import smu.nuda.domain.member.entity.enums.Role;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.global.error.DomainException;
import smu.nuda.domain.member.error.MemberErrorCode;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Member 탈퇴 도메인 모델 상태 전이 테스트")
class MemberWithdrawTest {

    /*
    Member 엔티티의 탈퇴 관련 상태 전이 메서드를 순수 단위 테스트로 검증함

    - requestWithdraw(clock) : ACTIVE → WITHDRAW_REQUESTED, deletedAt 기록
    - withdraw()              : WITHDRAW_REQUESTED → WITHDRAWN
    - cancelWithdraw(clock)   : WITHDRAW_REQUESTED → ACTIVE, deletedAt=null (기간 내)
    - isWithinCancellationWindow(clock) : deletedAt 기준 30일 이내 여부
     */

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private Clock fixedClockAt(LocalDateTime dateTime) {
        return Clock.fixed(dateTime.atZone(ZONE).toInstant(), ZONE);
    }

    private Member activeBuilder() {
        return Member.builder()
                .username("testuser")
                .email("test@test.com")
                .password("password")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("ACTIVE 멤버가 탈퇴 요청하면 status가 WITHDRAW_REQUESTED이 되고 deletedAt이 clock 기준으로 기록된다")
    void requestWithdraw_success() {
        // [given] ACTIVE 상태의 멤버, 고정된 시각의 clock
        Member member = activeBuilder();
        LocalDateTime fixedNow = LocalDateTime.of(2026, 1, 1, 12, 0, 0);
        Clock clock = fixedClockAt(fixedNow);

        // [when] 탈퇴 요청
        member.requestWithdraw(clock);

        // [then] 상태가 WITHDRAW_REQUESTED로 전이되고 deletedAt이 clock 기준 시각으로 기록됨
        assertThat(member.getStatus()).isEqualTo(Status.WITHDRAW_REQUESTED);
        assertThat(member.getDeletedAt()).isEqualTo(fixedNow);
    }

    @Test
    @DisplayName("ACTIVE가 아닌 멤버가 탈퇴 요청하면 INVALID_STATUS 예외가 발생한다")
    void requestWithdraw_fail_when_not_active() {
        // [given] INACTIVE 상태의 멤버
        Member member = Member.builder()
                .username("testuser")
                .email("test@test.com")
                .password("password")
                .role(Role.USER)
                .status(Status.INACTIVE)
                .build();
        Clock clock = fixedClockAt(LocalDateTime.now());

        // [when, then] INVALID_STATUS 예외 발생
        assertThatThrownBy(() -> member.requestWithdraw(clock))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(MemberErrorCode.INVALID_STATUS.getMessage());
    }

    @Test
    @DisplayName("WITHDRAW_REQUESTED 멤버가 withdraw() 호출 시 WITHDRAWN으로 전이된다")
    void withdraw_success() {
        // [given] WITHDRAW_REQUESTED 상태의 멤버
        Member member = activeBuilder();
        member.requestWithdraw(Clock.systemDefaultZone());

        // [when] 최종 탈퇴 처리
        member.withdraw();

        // [then] status가 WITHDRAWN으로 전이됨
        assertThat(member.getStatus()).isEqualTo(Status.WITHDRAWN);
    }

    @Test
    @DisplayName("WITHDRAW_REQUESTED가 아닌 멤버가 withdraw() 호출 시 NOT_IN_WITHDRAW_REQUESTED 예외가 발생한다")
    void withdraw_fail_when_not_withdraw_requested() {
        // [given] ACTIVE 상태의 멤버
        Member member = activeBuilder();

        // [when, then] NOT_IN_WITHDRAW_REQUESTED 예외 발생
        assertThatThrownBy(member::withdraw)
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(MemberErrorCode.NOT_IN_WITHDRAW_REQUESTED.getMessage());
    }

    @Test
    @DisplayName("WITHDRAW_REQUESTED 멤버가 30일 이내에 취소하면 ACTIVE로 복원되고 deletedAt이 null이 된다")
    void cancelWithdraw_success() {
        // [given] 오늘 탈퇴 요청한 멤버
        LocalDateTime requestTime = LocalDateTime.of(2026, 1, 1, 12, 0, 0);
        Member member = activeBuilder();
        member.requestWithdraw(fixedClockAt(requestTime));

        // [when] 탈퇴 취소 (같은 시각)
        member.cancelWithdraw(fixedClockAt(requestTime));

        // [then] ACTIVE로 복원되고 deletedAt이 null이 됨
        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(member.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("WITHDRAW_REQUESTED가 아닌 멤버가 cancelWithdraw() 호출 시 NOT_IN_WITHDRAW_REQUESTED 예외가 발생한다")
    void cancelWithdraw_fail_when_not_withdraw_requested() {
        // [given] ACTIVE 상태의 멤버
        Member member = activeBuilder();

        // [when, then] NOT_IN_WITHDRAW_REQUESTED 예외 발생
        assertThatThrownBy(() -> member.cancelWithdraw(Clock.systemDefaultZone()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(MemberErrorCode.NOT_IN_WITHDRAW_REQUESTED.getMessage());
    }

    @Test
    @DisplayName("탈퇴 취소 가능 기간이 지난 멤버가 cancelWithdraw() 호출 시 CANCELLATION_WINDOW_EXPIRED 예외가 발생한다")
    void cancelWithdraw_fail_when_window_expired() {
        // [given] 31일 전에 탈퇴 요청한 멤버
        LocalDateTime requestTime = LocalDateTime.of(2026, 1, 1, 12, 0, 0);
        Member member = activeBuilder();
        member.requestWithdraw(fixedClockAt(requestTime));

        // [when, then] CANCELLATION_WINDOW_EXPIRED 예외 발생
        assertThatThrownBy(() -> member.cancelWithdraw(fixedClockAt(requestTime.plusDays(31))))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(MemberErrorCode.CANCELLATION_WINDOW_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("deletedAt이 오늘이면 isWithinCancellationWindow()가 true를 반환한다")
    void isWithinCancellationWindow_true_when_today() {
        // [given] 오늘 탈퇴 요청한 멤버
        LocalDateTime requestTime = LocalDateTime.of(2026, 1, 1, 12, 0, 0);
        Member member = activeBuilder();
        member.requestWithdraw(fixedClockAt(requestTime));

        assertThat(member.isWithinCancellationWindow(fixedClockAt(requestTime))).isTrue();
    }

    @Test
    @DisplayName("deletedAt이 31일 전이면 isWithinCancellationWindow()가 false를 반환한다")
    void isWithinCancellationWindow_false_when_31_days_ago() {
        // [given] 31일 전에 탈퇴 요청한 멤버
        LocalDateTime requestTime = LocalDateTime.of(2026, 1, 1, 12, 0, 0);
        Member member = activeBuilder();
        member.requestWithdraw(fixedClockAt(requestTime));

        assertThat(member.isWithinCancellationWindow(fixedClockAt(requestTime.plusDays(31)))).isFalse();
    }

    @Test
    @DisplayName("deletedAt이 null이면 isWithinCancellationWindow()가 false를 반환한다")
    void isWithinCancellationWindow_false_when_deleted_at_null() {
        // [given] deletedAt이 null인 ACTIVE 멤버
        Member member = activeBuilder();

        assertThat(member.isWithinCancellationWindow(Clock.systemDefaultZone())).isFalse();
    }
}
