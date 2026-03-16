package smu.nuda.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.ingredient.entity.Ingredient;
import smu.nuda.domain.like.entity.IngredientLike;
import smu.nuda.domain.member.entity.Member;

import java.util.Optional;

public interface IngredientLikeRepository extends JpaRepository<IngredientLike, Long> {
    Optional<IngredientLike> findByMemberAndIngredient(Member member, Ingredient ingredient);
    Optional<IngredientLike> findByIngredientIdAndMemberId(Long ingredientId, Long memberId);

    @Modifying
    @Query("DELETE FROM IngredientLike il WHERE il.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);
}
