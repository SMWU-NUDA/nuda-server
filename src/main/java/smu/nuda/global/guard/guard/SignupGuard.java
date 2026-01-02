package smu.nuda.global.guard.guard;

import org.springframework.stereotype.Component;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.SignupStepType;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.global.error.DomainException;

@Component
public class SignupGuard {
    public void ensureCompleted(Member member) {
        if (member.getSignupStep() != SignupStepType.COMPLETED) {
            throw new DomainException(MemberErrorCode.SIGNUP_NOT_COMPLETED);
        }
    }

    public void ensureStepAllowed(Member member, SignupStepType requiredStep) {
        if (!member.getSignupStep().isAfterOrEqual(requiredStep)) {
            throw new DomainException(
                    MemberErrorCode.SIGNUP_STEP_REQUIRED,
                    java.util.Map.of(
                            "requiredStep", requiredStep.name(),
                            "currentStep", member.getSignupStep().name()
                    )
            );
        }
    }
}
