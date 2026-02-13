package smu.nuda.domain.like.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.like.dto.LikedIngredientResponse;

import java.util.List;

import static smu.nuda.domain.ingredient.entity.QIngredient.ingredient;
import static smu.nuda.domain.like.entity.QIngredientLike.ingredientLike;

@Repository
@RequiredArgsConstructor
public class IngredientLikeQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<LikedIngredientResponse> findLikedIngredients(Long memberId, Boolean preference, Long cursor, int size) {
        return queryFactory
                .select(Projections.constructor(
                        LikedIngredientResponse.class,
                        ingredientLike.id,
                        ingredient.id,
                        ingredient.name,
                        ingredient.riskLevel,
                        ingredient.layerType
                ))
                .from(ingredientLike)
                .join(ingredientLike.ingredient, ingredient)
                .where(
                        ingredientLike.member.id.eq(memberId),
                        ingredientLike.preference.eq(preference),
                        cursor != null ? ingredientLike.id.lt(cursor) : null
                )
                .orderBy(ingredientLike.id.desc())
                .limit(size + 1)
                .fetch();
    }

}
