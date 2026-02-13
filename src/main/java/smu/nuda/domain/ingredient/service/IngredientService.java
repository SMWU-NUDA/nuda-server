package smu.nuda.domain.ingredient.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.nuda.domain.ingredient.dto.IngredientSummaryResponse;
import smu.nuda.domain.ingredient.repository.IngredientQueryRepository;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientQueryRepository ingredientQueryRepository;

    public IngredientSummaryResponse getIngredientSummary(Long productId, Long memberId) {
        return ingredientQueryRepository.getProductIngredientSummary(productId, memberId);
    }
}
