package smu.nuda.domain.ingredient.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.ingredient.dto.ComponentCountRow;
import smu.nuda.domain.ingredient.dto.MyIngredientCountRow;
import smu.nuda.domain.ingredient.dto.RiskCountRow;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

import java.util.*;

import static smu.nuda.domain.product.entity.QProductIngredient.productIngredient;
import static smu.nuda.domain.ingredient.entity.QIngredient.ingredient;
import static smu.nuda.domain.component.entity.QComponent.component;
import static smu.nuda.domain.ingredient.entity.QIngredientLike.ingredientLike;

@Repository
@RequiredArgsConstructor
public class IngredientQueryRepository {
    private final JPAQueryFactory queryFactory;

    public int countTotal(Long productId) {
        Long count = queryFactory
                .select(productIngredient.count())
                .from(productIngredient)
                .where(productIngredient.product.id.eq(productId))
                .fetchOne();

        return Optional.ofNullable(count)
                .orElse(0L)
                .intValue();
    }

    public List<RiskCountRow> countByRiskLevel(Long productId) {
        return queryFactory
                .select(Projections.constructor(
                        RiskCountRow.class,
                        ingredient.riskLevel,
                        productIngredient.count()
                ))
                .from(productIngredient)
                .join(productIngredient.ingredient, ingredient)
                .where(productIngredient.product.id.eq(productId))
                .groupBy(ingredient.riskLevel)
                .fetch();
    }

    public List<ComponentCountRow> countByComponent(Long productId) {
        return queryFactory
                .select(Projections.constructor(
                        ComponentCountRow.class,
                        component.layerType.stringValue(),
                        productIngredient.count()
                ))
                .from(productIngredient)
                .join(productIngredient.component, component)
                .where(productIngredient.product.id.eq(productId))
                .groupBy(component.layerType)
                .fetch();
    }

    public List<MyIngredientCountRow> countMyIngredient(Long productId, Long memberId) {
        return queryFactory
                .select(Projections.constructor(
                        MyIngredientCountRow.class,
                        ingredientLike.preference,
                        productIngredient.count()
                ))
                .from(productIngredient)
                .join(ingredientLike)
                .on(productIngredient.ingredient.id.eq(ingredientLike.ingredient.id))
                .where(
                        productIngredient.product.id.eq(productId),
                        ingredientLike.member.id.eq(memberId)
                )
                .groupBy(ingredientLike.preference)
                .fetch();
    }

}
