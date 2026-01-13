package smu.nuda.domain.signupdraft.guard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.domain.signupdraft.error.SignupDraftErrorCode;
import smu.nuda.domain.signupdraft.policy.SignupDraftPolicy;
import smu.nuda.domain.signupdraft.repostiory.SignupDraftRepository;
import smu.nuda.global.error.DomainException;

@Component
@RequiredArgsConstructor
public class SignupDraftGuard {
    private final SignupDraftRepository signupDraftRepository;
    private final SignupDraftPolicy signupDraftPolicy = SignupDraftPolicy.INSTANCE;

    public SignupDraft validateStep(String signupToken, SignupStep requiredStep) {
        SignupDraft draft = signupDraftRepository.findBySignupToken(signupToken)
                .orElseThrow(() -> new DomainException(SignupDraftErrorCode.DRAFT_NOT_FOUND));

        signupDraftPolicy.validateAccess(draft, requiredStep);
        return draft;
    }
}
