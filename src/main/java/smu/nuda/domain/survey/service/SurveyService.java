package smu.nuda.domain.survey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.survey.dto.SurveyRequest;
import smu.nuda.domain.survey.entity.Survey;
import smu.nuda.domain.survey.error.SurveyErrorCode;
import smu.nuda.domain.survey.repository.SurveyRepository;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.guard.guard.AuthenticationGuard;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    @Transactional
    public Long submitSurvey(Member member, SurveyRequest request) {
        if (surveyRepository.existsByMemberId(member.getId())) {
            throw new DomainException(SurveyErrorCode.SURVEY_ALREADY_EXISTS);
        }

        Survey survey = Survey.builder()
                .member(member)
                .irritationLevel(request.getIrritationLevel())
                .scent(request.getScent())
                .changeFrequency(request.getChangeFrequency())
                .thickness(request.getThickness())
                .priority(request.getPriority())
                .build();

        surveyRepository.save(survey);
        member.completeSurvey();
        
        return survey.getId();
    }
}
