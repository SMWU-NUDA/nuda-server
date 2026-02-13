package smu.nuda.domain.ingredient.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.ingredient.dto.IngredientSummaryResponse;
import smu.nuda.domain.ingredient.entity.enums.LayerType;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static smu.nuda.domain.product.entity.QProductIngredient.productIngredient;
import static smu.nuda.domain.ingredient.entity.QIngredient.ingredient;
import static smu.nuda.domain.like.entity.QIngredientLike.ingredientLike;

@Repository
@RequiredArgsConstructor
public class IngredientQueryRepository {
    private final JPAQueryFactory queryFactory;

    public IngredientSummaryResponse getProductIngredientSummary(Long productId, Long memberId) {

        // totalCount
        Long totalCount = queryFactory
                .select(productIngredient.count())
                .from(productIngredient)
                .where(productIngredient.product.id.eq(productId))
                .fetchOne();

        // riskCounts
        List<Tuple> riskTuples = queryFactory
                .select(ingredient.riskLevel, ingredient.count())
                .from(productIngredient)
                .join(productIngredient.ingredient, ingredient)
                .where(productIngredient.product.id.eq(productId))
                .groupBy(ingredient.riskLevel)
                .fetch();

        // layerCounts
        List<Tuple> layerTuples = queryFactory
                .select(ingredient.layerType, ingredient.count())
                .from(productIngredient)
                .join(productIngredient.ingredient, ingredient)
                .where(productIngredient.product.id.eq(productId))
                .groupBy(ingredient.layerType)
                .fetch();

        // myIngredientCounts
        long prefer = 0;
        long avoid = 0;
        if (memberId != null) {

            List<Tuple> myTuples = queryFactory
                    .select(ingredientLike.preference, ingredientLike.count())
                    .from(ingredientLike)
                    .join(ingredientLike.ingredient, ingredient)
                    .join(productIngredient).on(productIngredient.ingredient.eq(ingredient))
                    .where(
                            ingredientLike.member.id.eq(memberId),
                            productIngredient.product.id.eq(productId)
                    )
                    .groupBy(ingredientLike.preference)
                    .fetch();

            for (Tuple t : myTuples) {
                Boolean preference = t.get(ingredientLike.preference);
                Long count = t.get(ingredientLike.count());

                if (Boolean.TRUE.equals(preference)) {
                    prefer = count;
                } else {
                    avoid = count;
                }
            }
        }

        Map<RiskLevel, Long> riskMap = initRiskMap();
        for (Tuple t : riskTuples) {
            riskMap.put(
                    t.get(ingredient.riskLevel),
                    t.get(ingredient.count())
            );
        }

        Map<LayerType, Long> layerMap = initLayerMap();
        for (Tuple t : layerTuples) {
            layerMap.put(
                    t.get(ingredient.layerType),
                    t.get(ingredient.count())
            );
        }

        return IngredientSummaryResponse.builder()
                .productId(productId)
                .totalCount(totalCount != null ? totalCount.intValue() : 0)
                .riskCounts(riskMap)
                .ingredientCounts(layerMap)
                .myIngredientCounts(
                        IngredientSummaryResponse.MyIngredientLikeSummary.builder()
                                .prefer(prefer)
                                .avoided(avoid)
                                .build()
                )
                .build();
    }

    private Map<RiskLevel, Long> initRiskMap() {
        Map<RiskLevel, Long> map = new EnumMap<>(RiskLevel.class);
        for (RiskLevel r : RiskLevel.values()) {
            map.put(r, 0L);
        }
        return map;
    }

    private Map<LayerType, Long> initLayerMap() {
        Map<LayerType, Long> map = new EnumMap<>(LayerType.class);
        for (LayerType l : LayerType.values()) {
            map.put(l, 0L);
        }
        return map;
    }
}
