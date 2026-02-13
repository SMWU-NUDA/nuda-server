package smu.nuda.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.ingredient.entity.Ingredient;
import smu.nuda.domain.like.entity.IngredientLike;
import smu.nuda.domain.member.entity.Member;

import java.util.Optional;

public interface IngredientLikeRepository extends JpaRepository<IngredientLike, Long> {
    Optional<IngredientLike> findByMemberAndIngredient(Member member, Ingredient ingredient);
    void deleteByMemberAndIngredient(Member member, Ingredient ingredient);
}
