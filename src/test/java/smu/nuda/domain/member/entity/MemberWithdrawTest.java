package smu.nuda.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import smu.nuda.domain.member.entity.enums.Role;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.global.error.DomainException;
import smu.nuda.domain.member.error.MemberErrorCode;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Member 탈퇴 도메인 모델 상태 전이 테스트")
class MemberWithdrawTest {

    /*
    Member 엔티티의 탈퇴 관련 상태 전이 메서드를 순수 단위 테스트로 검증함

    - requestWithdraw(clock) : ACTIVE → WITHDRAW_REQUESTED, deletedAt 기록
    - withdraw()              : → WITHDRAWN
    - cancelWithdraw()        : WITHDRAW_REQUESTED → ACTIVE, deletedAt=null
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
    @DisplayName("withdraw() 호출 시 status가 WITHDRAWN으로 전이된다")
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
    @DisplayName("cancelWithdraw() 호출 시 status가 ACTIVE로 복원되고 deletedAt이 null이 된다")
    void cancelWithdraw_success() {
        // [given] WITHDRAW_REQUESTED 상태의 멤버
        Member member = activeBuilder();
        member.requestWithdraw(Clock.systemDefaultZone());

        // [when] 탈퇴 취소
        member.cancelWithdraw();

        // [then] ACTIVE로 복원되고 deletedAt이 null이 됨
        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(member.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("deletedAt이 오늘이면 isWithinCancellationWindow()가 true를 반환한다")
    void isWithinCancellationWindow_true_when_today() {
        // [given] 오늘 탈퇴 요청한 멤버 (clock 고정)
        LocalDateTime requestTime = LocalDateTime.of(2026, 1, 1, 12, 0, 0);
        Clock requestClock = fixedClockAt(requestTime);

        Member member = activeBuilder();
        member.requestWithdraw(requestClock);

        // [when] 같은 시각의 clock으로 취소 가능 여부 확인
        assertThat(member.isWithinCancellationWindow(requestClock)).isTrue();
    }

    @Test
    @DisplayName("deletedAt이 31일 전이면 isWithinCancellationWindow()가 false를 반환한다")
    void isWithinCancellationWindow_false_when_31_days_ago() {
        // [given] 31일 전에 탈퇴 요청한 멤버
        LocalDateTime requestTime = LocalDateTime.of(2026, 1, 1, 12, 0, 0);
        Member member = activeBuilder();
        member.requestWithdraw(fixedClockAt(requestTime));

        // [when] 31일 이후 clock으로 취소 가능 여부 확인
        Clock laterClock = fixedClockAt(requestTime.plusDays(31));
        assertThat(member.isWithinCancellationWindow(laterClock)).isFalse();
    }

    @Test
    @DisplayName("deletedAt이 null이면 isWithinCancellationWindow()가 false를 반환한다")
    void isWithinCancellationWindow_false_when_deleted_at_null() {
        // [given] deletedAt이 null인 ACTIVE 멤버
        Member member = activeBuilder();

        // [when, then] 취소 불가
        assertThat(member.isWithinCancellationWindow(Clock.systemDefaultZone())).isFalse();
    }
}
