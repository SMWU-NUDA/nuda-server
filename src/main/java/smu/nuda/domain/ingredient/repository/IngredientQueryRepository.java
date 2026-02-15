package smu.nuda.domain.ingredient.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.ingredient.dto.IngredientSummaryResponse;
import smu.nuda.domain.ingredient.entity.enums.LayerType;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.global.error.DomainException;

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

        // layer와 risk를 그룹 조회
        List<Tuple> tuples = queryFactory
                .select(
                        ingredient.layerType,
                        ingredient.riskLevel,
                        ingredient.count()
                )
                .from(productIngredient)
                .join(productIngredient.ingredient, ingredient)
                .where(productIngredient.product.id.eq(productId))
                .groupBy(ingredient.layerType, ingredient.riskLevel)
                .fetch();

        if ((totalCount == null || totalCount == 0) && tuples.isEmpty()) throw new DomainException(ProductErrorCode.INVALID_PRODUCT);

        Map<LayerType, IngredientSummaryResponse.LayerSummary> layerSummary = buildLayerSummary(tuples);
        Map<RiskLevel, Long> globalRiskSummary = buildGlobalRiskSummary(tuples);
        IngredientSummaryResponse.MyIngredientLikeSummary mySummary = buildMySummary(productId, memberId);

        return IngredientSummaryResponse.builder()
                .productId(productId)
                .totalCount(totalCount != null ? totalCount.intValue() : 0)
                .globalRiskCounts(globalRiskSummary)
                .ingredientCounts(layerSummary)
                .myIngredientCounts(mySummary)
                .build();
    }

    private Map<RiskLevel, Long> initRiskMap() {
        Map<RiskLevel, Long> map = new EnumMap<>(RiskLevel.class);
        for (RiskLevel r : RiskLevel.values()) {
            map.put(r, 0L);
        }
        return map;
    }

    private Map<LayerType, IngredientSummaryResponse.LayerSummary> buildLayerSummary(List<Tuple> tuples) {
        Map<LayerType, IngredientSummaryResponse.LayerSummary> layerMap = new EnumMap<>(LayerType.class);

        // 초기화
        for (LayerType layer : LayerType.values()) {
            layerMap.put(layer,
                    new IngredientSummaryResponse.LayerSummary(initRiskMap()));
        }

        for (Tuple t : tuples) {
            LayerType layer = t.get(ingredient.layerType);
            RiskLevel risk = t.get(ingredient.riskLevel);
            Long count = t.get(ingredient.count());

            IngredientSummaryResponse.LayerSummary summary = layerMap.get(layer);

            summary.increaseCount(count);
            summary.getRiskCounts().put(risk, count);
        }

        return layerMap;
    }

    private Map<RiskLevel, Long> buildGlobalRiskSummary(List<Tuple> tuples) {
        Map<RiskLevel, Long> riskMap = initRiskMap();

        for (Tuple t : tuples) {
            RiskLevel risk = t.get(ingredient.riskLevel);
            Long count = t.get(ingredient.count());

            riskMap.put(risk, riskMap.get(risk) + count);
        }

        return riskMap;
    }

    private IngredientSummaryResponse.MyIngredientLikeSummary buildMySummary(Long productId, Long memberId) {
        if (memberId == null) {
            return IngredientSummaryResponse.MyIngredientLikeSummary.builder()
                    .prefer(0)
                    .avoided(0)
                    .build();
        }

        long prefer = 0;
        long avoid = 0;

        List<Tuple> myTuples = queryFactory
                .select(ingredientLike.preference, ingredientLike.count())
                .from(ingredientLike)
                .join(ingredientLike.ingredient, ingredient)
                .join(productIngredient)
                .on(productIngredient.ingredient.eq(ingredient))
                .where(
                        ingredientLike.member.id.eq(memberId),
                        productIngredient.product.id.eq(productId)
                )
                .groupBy(ingredientLike.preference)
                .fetch();

        for (Tuple t : myTuples) {
            Boolean preference = t.get(ingredientLike.preference);
            Long count = t.get(ingredientLike.count());

            if (Boolean.TRUE.equals(preference)) prefer = count;
            else avoid = count;
        }

        return IngredientSummaryResponse.MyIngredientLikeSummary.builder()
                .prefer(prefer)
                .avoided(avoid)
                .build();
    }

}
