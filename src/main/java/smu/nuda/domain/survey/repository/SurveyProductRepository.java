package smu.nuda.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.survey.entity.SurveyProduct;

public interface SurveyProductRepository extends JpaRepository<SurveyProduct, Long> {
}
