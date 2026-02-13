package smu.nuda.domain.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.ingredient.dto.IngredientItem;

import java.util.List;

import static smu.nuda.domain.ingredient.entity.QIngredient.ingredient;
import static smu.nuda.domain.product.entity.QProductIngredient.productIngredient;

@Repository
@RequiredArgsConstructor
public class ProductIngredientQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<IngredientItem> findIngredientsByProductId(Long productId) {
        return queryFactory
                .select(
                        com.querydsl.core.types.Projections.constructor(
                                IngredientItem.class,
                                ingredient.id,
                                ingredient.name,
                                ingredient.riskLevel,
                                ingredient.layerType
                        )
                )
                .from(productIngredient)
                .join(productIngredient.ingredient, ingredient)
                .where(productIngredient.product.id.eq(productId))
                .fetch();
    }
}
