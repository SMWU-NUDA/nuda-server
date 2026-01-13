package smu.nuda.domain.signupdraft.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.signupdraft.dto.SignupDraftCreateResponse;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.signupdraft.repostiory.SignupDraftRepository;

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
}

