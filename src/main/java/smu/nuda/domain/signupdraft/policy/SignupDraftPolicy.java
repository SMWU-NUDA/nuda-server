package smu.nuda.domain.signupdraft.policy;

import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.domain.signupdraft.error.SignupDraftErrorCode;
import smu.nuda.global.error.DomainException;

import java.util.Map;

public class SignupDraftPolicy {
    public static final SignupDraftPolicy INSTANCE = new SignupDraftPolicy();

    public void validateAccess(SignupDraft draft, SignupStep requiredStep) {
        if (!isAllowed(draft, requiredStep)) {
            throw new DomainException(
                    SignupDraftErrorCode.DRAFT_NOT_COMPLETED,
                    Map.of(
                            "requiredStep", requiredStep.name(),
                            "currentStep", draft.getCurrentStep().name()
                    )
            );
        }
    }

    public boolean isAllowed(SignupDraft draft, SignupStep requiredStep) {
        return draft.getCurrentStep().isAfterOrEqual(requiredStep);
    }
}
