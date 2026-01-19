package smu.nuda.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.survey.entity.Survey;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    boolean existsByMemberId(Long memberId);
    Optional<Survey> findByMember(Member member);
}
