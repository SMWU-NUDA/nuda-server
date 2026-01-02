package smu.nuda.domain.member.withdraw.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WithdrawRequestedEvent {
    private final Long memberId;

}
