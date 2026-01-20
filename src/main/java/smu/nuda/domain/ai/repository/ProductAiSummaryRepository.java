package smu.nuda.domain.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.ai.entity.ProductAiSummary;

public interface ProductAiSummaryRepository extends JpaRepository<ProductAiSummary, Long> {
}
