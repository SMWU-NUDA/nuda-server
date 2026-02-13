package smu.nuda.domain.ingredient.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.ingredient.dto.IngredientItem;
import smu.nuda.domain.ingredient.dto.IngredientResponse;
import smu.nuda.domain.ingredient.dto.IngredientSummaryResponse;
import smu.nuda.domain.ingredient.repository.IngredientQueryRepository;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductIngredientQueryRepository;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.error.DomainException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final ProductRepository productRepository;
    private final IngredientQueryRepository ingredientQueryRepository;
    private final ProductIngredientQueryRepository productIngredientQueryRepository;

    @Transactional(readOnly = true)
    public IngredientSummaryResponse getIngredientSummary(Long productId, Long memberId) {
        if (!productRepository.existsById(productId)) throw new DomainException(ProductErrorCode.INVALID_PRODUCT);
        return ingredientQueryRepository.getProductIngredientSummary(productId, memberId);
    }

    @Transactional(readOnly = true)
    public IngredientResponse getIngredients(Long productId) {
        if (!productRepository.existsById(productId)) throw new DomainException(ProductErrorCode.INVALID_PRODUCT);

        List<IngredientItem> ingredients = productIngredientQueryRepository.findIngredientsByProductId(productId);
        return new IngredientResponse(
                (long) ingredients.size(),
                ingredients
        );
    }
}
