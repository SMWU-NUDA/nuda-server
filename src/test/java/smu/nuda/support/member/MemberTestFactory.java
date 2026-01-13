package smu.nuda.support.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Role;
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
                Role.USER
        );
    }

    public Member inactive() {
        return save(
                "inactive",
                Status.INACTIVE,
                Role.USER
        );
    }

    public Member admin() {
        return save(
                "active",
                Status.ACTIVE,
                Role.ADMIN
        );
    }

    private Member save(String prefix, Status status, Role role) {
        long seq = SEQUENCE.incrementAndGet();

        return memberRepository.save(
                Member.builder()
                        .username(prefix + "_" + seq)
                        .email(prefix + "_" + seq + "@test.com")
                        .password("password")
                        .role(role)
                        .status(status)
                        .build()
        );
    }
}

