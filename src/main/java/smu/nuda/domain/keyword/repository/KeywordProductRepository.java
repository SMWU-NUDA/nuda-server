package smu.nuda.domain.keyword.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.keyword.entity.KeywordProduct;

public interface KeywordProductRepository extends JpaRepository<KeywordProduct, Long> {

}
