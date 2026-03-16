package smu.nuda.support.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Role;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.repository.MemberRepository;

import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class MemberTestFactory {

    public static final String RAW_PASSWORD = "password";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
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

    public Member withdrawRequested() {
        Member member = save("withdraw_requested", Status.WITHDRAW_REQUESTED, Role.USER);
        // deletedAt을 지금으로 세팅
        try {
            var field = Member.class.getDeclaredField("deletedAt");
            field.setAccessible(true);
            field.set(member, java.time.LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return memberRepository.save(member);
    }

    public Member withdrawn() {
        return save("withdrawn", Status.WITHDRAWN, Role.USER);
    }

    private Member save(String prefix, Status status, Role role) {
        long seq = SEQUENCE.incrementAndGet();

        return memberRepository.save(
                Member.builder()
                        .username(prefix + "_" + seq)
                        .email(prefix + "_" + seq + "@test.com")
                        .password(passwordEncoder.encode(RAW_PASSWORD))
                        .role(role)
                        .status(status)
                        .build()
        );
    }
}

