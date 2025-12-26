package smu.nuda.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.survey.entity.Survey;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
}
