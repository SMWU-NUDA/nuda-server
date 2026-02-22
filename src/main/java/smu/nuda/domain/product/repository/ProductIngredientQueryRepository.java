package smu.nuda.domain.product.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.ingredient.dto.IngredientItem;
import smu.nuda.domain.ingredient.dto.enums.IngredientFilterType;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

import java.util.List;

import static smu.nuda.domain.ingredient.entity.QIngredient.ingredient;
import static smu.nuda.domain.product.entity.QProductIngredient.productIngredient;
import static smu.nuda.domain.like.entity.QIngredientLike.ingredientLike;

@Repository
@RequiredArgsConstructor
public class ProductIngredientQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<IngredientItem> findIngredients(Long productId, IngredientFilterType filterType, Long memberId) {
        JPAQuery<IngredientItem> query = queryFactory
                .select(Projections.constructor(
                        IngredientItem.class,
                        ingredient.id,
                        ingredient.name,
                        ingredient.riskLevel,
                        ingredient.layerType
                ))
                .from(productIngredient)
                .join(productIngredient.ingredient, ingredient)
                .where(productIngredient.product.id.eq(productId));

        if (filterType == null || filterType == IngredientFilterType.ALL) {
            return query.fetch();
        }

        // Risk 필터
        else if (filterType.isRiskFilter()) {
            query.where(ingredient.riskLevel.eq(filterType.getRiskLevel()));
        }

        // 관심 성분
        else if (filterType.isInterest()) {
            query.join(ingredientLike)
                    .on(
                            ingredientLike.ingredient.id.eq(ingredient.id)
                                    .and(ingredientLike.member.id.eq(memberId))
                                    .and(ingredientLike.preference.isTrue())
                    );
        }

        // 피할 성분
        else if (filterType.isAvoid()) {
            query.join(ingredientLike)
                    .on(
                            ingredientLike.ingredient.id.eq(ingredient.id)
                                    .and(ingredientLike.member.id.eq(memberId))
                                    .and(ingredientLike.preference.isFalse())
                    );
        }
        return query.fetch();
    }
}
