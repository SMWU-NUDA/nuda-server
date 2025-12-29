package smu.nuda.domain.survey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.domain.survey.dto.SurveyCreateRequest;
import smu.nuda.domain.survey.entity.Survey;
import smu.nuda.domain.survey.error.SurveyErrorCode;
import smu.nuda.domain.survey.repository.SurveyRepository;
import smu.nuda.global.error.DomainException;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long submitSurvey(Long memberId, SurveyCreateRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new DomainException(SurveyErrorCode.SURVEY_MEMBER_NOT_FOUND));

        if (surveyRepository.existsByMemberId(memberId)) {
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
        return survey.getId();
    }
}
