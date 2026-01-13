package smu.nuda.domain.signupdraft.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.dto.DeliveryRequest;
import smu.nuda.domain.signupdraft.dto.AccountRequest;
import smu.nuda.domain.signupdraft.dto.SignupDraftCreateResponse;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.signupdraft.error.SignupDraftErrorCode;
import smu.nuda.domain.signupdraft.repostiory.SignupDraftRepository;
import smu.nuda.domain.survey.dto.SurveyRequest;
import smu.nuda.global.error.DomainException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SignupDraftUseCase {

    private final SignupDraftRepository signupDraftRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

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

    public void updateAccount(String signupToken, AccountRequest request) {
        SignupDraft draft = signupDraftRepository.findBySignupToken(signupToken)
                .orElseThrow(() -> new DomainException(SignupDraftErrorCode.DRAFT_NOT_FOUND));
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        draft.updateAccount(
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getUsername()
        );
    }

    public void updateDelivery(String signupToken, DeliveryRequest request) {
        SignupDraft draft = signupDraftRepository.findBySignupToken(signupToken)
                .orElseThrow(() -> new DomainException(SignupDraftErrorCode.DRAFT_NOT_FOUND));

        draft.updateDelivery(
                request.getRecipient(),
                request.getPhoneNum(),
                request.getPostalCode(),
                request.getAddress1(),
                request.getAddress2()
        );
    }

    public void updateSurvey(String signupToken, SurveyRequest request) {

        SignupDraft draft = signupDraftRepository.findBySignupToken(signupToken)
                .orElseThrow(() -> new DomainException(SignupDraftErrorCode.DRAFT_NOT_FOUND));
        String productIdsJson = convertToJson(request.getProductIds());

        draft.updateSurvey(
                request.getIrritationLevel(),
                request.getScent(),
                request.getChangeFrequency(),
                request.getThickness(),
                request.getPriority(),
                productIdsJson
        );
    }

    private String convertToJson(List<Long> ids) {
        try {
            return objectMapper.writeValueAsString(ids);
        } catch (JsonProcessingException e) {
            throw new DomainException(SignupDraftErrorCode.INVALID_SURVEY_FORMAT);
        }
    }

}
