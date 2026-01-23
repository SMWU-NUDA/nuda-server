package smu.nuda.domain.ingredient.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.nuda.domain.ingredient.dto.*;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;
import smu.nuda.domain.ingredient.repository.IngredientQueryRepository;
import smu.nuda.domain.member.entity.Member;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientQueryRepository ingredientQueryRepository;

    public IngredientSummaryResponse getIngredientSummary(Long productId, Member member) {

        long totalCount = ingredientQueryRepository.countTotal(productId);
        Map<RiskLevel, Integer> riskCounts = ingredientQueryRepository.countByRiskLevel(productId).stream()
                .collect(Collectors.toMap(
                        RiskCountRow::getRiskLevel,
                        row -> (int) row.getCount()
                ));
        Map<String, Integer> componentCounts = ingredientQueryRepository.countByComponent(productId).stream()
                .collect(Collectors.toMap(
                        ComponentCountRow::getLayerType,
                        row -> (int) row.getCount()
                ));

        MyIngredientSummary myIngredient = null;
        if (member != null) {
            int prefer = 0;
            int avoided = 0;

            for (MyIngredientCountRow row : ingredientQueryRepository.countMyIngredient(productId, member.getId())) {
                if (row.isPreference()) prefer = (int) row.getCount();
                else avoided = (int) row.getCount();
            }

            myIngredient = new MyIngredientSummary(prefer, avoided);
        }

        return new IngredientSummaryResponse(
                productId,
                (int) totalCount,
                riskCounts,
                componentCounts,
                myIngredient
        );
    }
}
