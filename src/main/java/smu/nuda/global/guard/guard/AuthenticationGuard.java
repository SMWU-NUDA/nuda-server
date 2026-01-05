package smu.nuda.global.guard.guard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.security.principal.CustomUserDetails;

@Component
@RequiredArgsConstructor
public class AuthenticationGuard {

    private final MemberRepository memberRepository;

    public Member currentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new DomainException(AuthErrorCode.AUTH_REQUIRED);
        }

        return memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public void ensureLogin() {
        currentMember();
    }
}
