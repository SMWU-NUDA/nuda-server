package smu.nuda.global.guard.guard;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.security.CustomUserDetails;

@Component
public class AuthenticationGuard {
    public Member currentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new DomainException(AuthErrorCode.AUTH_REQUIRED);
        }

        return userDetails.getMember();
    }

    public void ensureLogin() {
        currentMember();
    }
}
