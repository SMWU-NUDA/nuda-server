package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.product.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
