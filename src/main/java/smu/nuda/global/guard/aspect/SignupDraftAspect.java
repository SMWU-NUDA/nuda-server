package smu.nuda.global.guard.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.domain.signupdraft.error.SignupDraftErrorCode;
import smu.nuda.domain.signupdraft.guard.SignupDraftGuard;
import smu.nuda.domain.signupdraft.guard.annotation.SignupDraftStep;
import smu.nuda.global.error.DomainException;

@Aspect
@Component
@RequiredArgsConstructor
public class SignupDraftAspect {

    private static final String SIGNUP_TOKEN_HEADER = "Signup-Token";
    private final SignupDraftGuard signupDraftGuard;

    @Before("@annotation(stepRequired)")
    public void checkStep(SignupDraftStep stepRequired) {
        String signupToken = extractSignupToken();
        SignupStep requiredStep = stepRequired.value();
        signupDraftGuard.validateStep(signupToken, requiredStep);
    }

    private String extractSignupToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) throw new DomainException(SignupDraftErrorCode.DRAFT_NOT_FOUND);

        HttpServletRequest request = attrs.getRequest();
        String token = request.getHeader(SIGNUP_TOKEN_HEADER);
        if (token == null || token.isBlank()) throw new DomainException(SignupDraftErrorCode.MISSING_TOKEN);

        return token;
    }
}
