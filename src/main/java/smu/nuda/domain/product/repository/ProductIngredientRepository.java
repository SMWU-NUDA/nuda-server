package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.product.entity.ProductIngredient;

public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Integer> {
}
