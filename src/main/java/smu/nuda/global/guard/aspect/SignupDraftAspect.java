package smu.nuda.global.guard.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import smu.nuda.global.guard.annotation.LoginRequired;
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


}
