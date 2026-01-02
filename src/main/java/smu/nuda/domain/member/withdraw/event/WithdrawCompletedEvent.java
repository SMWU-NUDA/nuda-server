package smu.nuda.domain.member.withdraw.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WithdrawCompletedEvent {
    private final Long memberId;
}
