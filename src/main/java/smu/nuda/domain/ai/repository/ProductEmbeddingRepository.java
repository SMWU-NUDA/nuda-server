package smu.nuda.domain.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.ai.entity.ProductEmbedding;

public interface ProductEmbeddingRepository extends JpaRepository<ProductEmbedding, Long> {
}
