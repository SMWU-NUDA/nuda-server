package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.product.entity.ProductAiSummary;

public interface ProductAiSummaryRepository extends JpaRepository<ProductAiSummary, Long> {
}
