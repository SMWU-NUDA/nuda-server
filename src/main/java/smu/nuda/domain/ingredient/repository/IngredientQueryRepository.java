package smu.nuda.domain.ingredient.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.ingredient.dto.IngredientSummaryResponse;
import smu.nuda.domain.ingredient.entity.QIngredient;
import smu.nuda.domain.ingredient.entity.enums.LayerType;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;
import smu.nuda.domain.like.entity.QIngredientLike;
import smu.nuda.domain.product.entity.QProductIngredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class IngredientQueryRepository {
    private final JPAQueryFactory queryFactory;

    public IngredientSummaryResponse getProductIngredientSummary(Long productId, Long memberId) {

        QProductIngredient pi = QProductIngredient.productIngredient;
        QIngredient i = QIngredient.ingredient;
        QIngredientLike il = QIngredientLike.ingredientLike;

        // totalCount
        Long totalCount = queryFactory
                .select(pi.count())
                .from(pi)
                .where(pi.product.id.eq(productId))
                .fetchOne();

        // riskCounts
        List<Tuple> riskTuples = queryFactory
                .select(i.riskLevel, i.count())
                .from(pi)
                .join(pi.ingredient, i)
                .where(pi.product.id.eq(productId))
                .groupBy(i.riskLevel)
                .fetch();

        // layerCounts
        List<Tuple> layerTuples = queryFactory
                .select(i.layerType, i.count())
                .from(pi)
                .join(pi.ingredient, i)
                .where(pi.product.id.eq(productId))
                .groupBy(i.layerType)
                .fetch();

        // myIngredientCounts
        long prefer = 0;
        long avoid = 0;
        if (memberId != null) {

            List<Tuple> myTuples = queryFactory
                    .select(il.preference, il.count())
                    .from(il)
                    .join(il.ingredient, i)
                    .join(pi).on(pi.ingredient.eq(i))
                    .where(
                            il.member.id.eq(memberId),
                            pi.product.id.eq(productId)
                    )
                    .groupBy(il.preference)
                    .fetch();

            for (Tuple t : myTuples) {
                Boolean preference = t.get(il.preference);
                Long count = t.get(il.count());

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
                    t.get(i.riskLevel),
                    t.get(i.count())
            );
        }

        Map<LayerType, Long> layerMap = initLayerMap();
        for (Tuple t : layerTuples) {
            layerMap.put(
                    t.get(i.layerType),
                    t.get(i.count())
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
