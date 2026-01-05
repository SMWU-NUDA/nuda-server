package smu.nuda.support.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Role;
import smu.nuda.domain.member.entity.enums.SignupStepType;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.repository.MemberRepository;

import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class MemberTestFactory {

    private final MemberRepository memberRepository;
    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    public Member active() {
        return save(
                "active",
                Status.ACTIVE,
                SignupStepType.COMPLETED
        );
    }

    public Member inactive() {
        return save(
                "inactive",
                Status.INACTIVE,
                SignupStepType.COMPLETED
        );
    }

    public Member signupIncomplete(SignupStepType step) {
        return save(
                "signup_" + step.name().toLowerCase(),
                Status.ACTIVE,
                step
        );
    }

    private Member save(String prefix, Status status, SignupStepType step) {
        long seq = SEQUENCE.incrementAndGet();

        return memberRepository.save(
                Member.builder()
                        .username(prefix + "_" + seq)
                        .email(prefix + "_" + seq + "@test.com")
                        .password("password")
                        .role(Role.USER)
                        .status(status)
                        .signupStep(step)
                        .build()
        );
    }
}

