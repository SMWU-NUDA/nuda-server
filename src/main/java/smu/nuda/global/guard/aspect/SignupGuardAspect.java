package smu.nuda.global.guard.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.SignupStepType;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.annotation.SignupCompleted;
import smu.nuda.global.guard.annotation.SignupStep;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.guard.guard.SignupGuard;

@Aspect
@Component
@RequiredArgsConstructor
public class SignupGuardAspect {

    private final AuthenticationGuard authenticationGuard;
    private final SignupGuard signupGuard;

    @Before("@annotation(loginRequired)")
    public void loginRequired(LoginRequired loginRequired) {
        authenticationGuard.ensureLogin();
    }

    @Before("@annotation(signupCompleted)")
    public void signupCompleted(SignupCompleted signupCompleted) {
        Member member = authenticationGuard.currentMember();
        signupGuard.ensureCompleted(member);
    }

    @Before("@annotation(signupStep)")
    public void signupStep(SignupStep signupStep) {
        Member member = authenticationGuard.currentMember();
        SignupStepType requiredStep = signupStep.value();

        signupGuard.ensureStepAllowed(member, requiredStep);
    }
}
