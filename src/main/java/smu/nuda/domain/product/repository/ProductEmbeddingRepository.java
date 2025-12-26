package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.product.entity.ProductEmbedding;

public interface ProductEmbeddingRepository extends JpaRepository<ProductEmbedding, Long> {
}
