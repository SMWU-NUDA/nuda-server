package smu.nuda.domain.ingredient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.ingredient.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

}
