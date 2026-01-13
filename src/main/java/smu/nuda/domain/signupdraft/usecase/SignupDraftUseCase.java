package smu.nuda.domain.signupdraft.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.dto.DeliveryRequest;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.signupdraft.dto.AccountRequest;
import smu.nuda.domain.signupdraft.dto.SignupDraftResponse;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.domain.signupdraft.error.SignupDraftErrorCode;
import smu.nuda.domain.signupdraft.repository.SignupDraftRepository;
import smu.nuda.domain.survey.dto.SurveyRequest;
import smu.nuda.domain.survey.entity.Survey;
import smu.nuda.domain.survey.entity.SurveyProduct;
import smu.nuda.domain.survey.repository.SurveyProductRepository;
import smu.nuda.domain.survey.repository.SurveyRepository;
import smu.nuda.global.error.DomainException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SignupDraftUseCase {

    private final SignupDraftRepository signupDraftRepository;
    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final ProductRepository productRepository;
    private final SurveyProductRepository surveyProductRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public SignupDraftResponse createDraft() {
        String signupToken = UUID.randomUUID().toString();

        SignupDraft draft = SignupDraft.create(signupToken);
        signupDraftRepository.save(draft);

        return SignupDraftResponse.builder()
                .signupToken(draft.getSignupToken())
                .currentStep(draft.getCurrentStep())
                .build();
    }

    @Transactional(readOnly = true)
    public SignupDraftResponse getDraft(String signupToken) {
        SignupDraft draft = signupDraftRepository.findBySignupToken(signupToken)
                .orElseThrow(() -> new DomainException(SignupDraftErrorCode.DRAFT_NOT_FOUND));

        return SignupDraftResponse.builder()
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
            throw new DomainException(SignupDraftErrorCode.JSON_SERIALIZATION_FAILED);
        }
    }

    public void commit(String signupToken) {
        SignupDraft draft = signupDraftRepository.findBySignupToken(signupToken)
                .orElseThrow(() -> new DomainException(SignupDraftErrorCode.MISSING_TOKEN));

        if (draft.getCurrentStep() != SignupStep.COMPLETED) {
            throw new DomainException(SignupDraftErrorCode.DRAFT_NOT_COMPLETED);
        }

        // 도메인 생성
        Member member = Member.from(draft);
        memberRepository.save(member);

        Survey survey = Survey.of(draft, member);
        surveyRepository.save(survey);

        // Todo. 엔티티 검증하는 통합 Repository 구현
        List<Long> productIds = draft.parseToProductIdList(objectMapper);
        List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) throw new DomainException(SignupDraftErrorCode.INVALID_SURVEY_PRODUCT_SELECTION);

        List<SurveyProduct> surveyProductList = SurveyProduct.of(survey, products);
        surveyProductRepository.saveAll(surveyProductList);

        // Draft 종료
        signupDraftRepository.delete(draft);
    }

}
