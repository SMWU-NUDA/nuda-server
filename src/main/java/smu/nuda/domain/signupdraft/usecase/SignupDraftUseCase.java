package smu.nuda.domain.signupdraft.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.signupdraft.dto.SignupDraftCreateResponse;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.signupdraft.error.SignupDraftErrorCode;
import smu.nuda.domain.signupdraft.repostiory.SignupDraftRepository;
import smu.nuda.global.error.DomainException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SignupDraftUseCase {

    private final SignupDraftRepository signupDraftRepository;

    public SignupDraftCreateResponse createDraft() {
        String signupToken = UUID.randomUUID().toString();

        SignupDraft draft = SignupDraft.create(signupToken);
        signupDraftRepository.save(draft);

        return SignupDraftCreateResponse.builder()
                .signupToken(draft.getSignupToken())
                .currentStep(draft.getCurrentStep())
                .build();
    }

    @Transactional(readOnly = true)
    public SignupDraftCreateResponse getDraft(String signupToken) {
        SignupDraft draft = signupDraftRepository.findBySignupToken(signupToken)
                .orElseThrow(() -> new DomainException(SignupDraftErrorCode.DRAFT_NOT_FOUND));

        return SignupDraftCreateResponse.builder()
                .signupToken(draft.getSignupToken())
                .currentStep(draft.getCurrentStep())
                .build();
    }

}

