package smu.nuda.domain.ingredient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.ingredient.dto.*;
import smu.nuda.domain.ingredient.dto.enums.IngredientFilterType;
import smu.nuda.domain.ingredient.entity.Ingredient;
import smu.nuda.domain.ingredient.error.IngredientErrorCode;
import smu.nuda.domain.ingredient.repository.HazardQueryRepository;
import smu.nuda.domain.ingredient.repository.IngredientQueryRepository;
import smu.nuda.domain.ingredient.repository.IngredientRepository;
import smu.nuda.domain.like.repository.IngredientLikeRepository;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductIngredientQueryRepository;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.error.DomainException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngredientService {

    private final ProductRepository productRepository;
    private final IngredientQueryRepository ingredientQueryRepository;
    private final ProductIngredientQueryRepository productIngredientQueryRepository;

    private final IngredientRepository ingredientRepository;
    private final IngredientLikeRepository ingredientLikeRepository;
    private final HazardQueryRepository hazardQueryRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public IngredientSummaryResponse getIngredientSummary(Long productId, Long memberId) {
        return ingredientQueryRepository.getProductIngredientSummary(productId, memberId);
    }

    @Transactional(readOnly = true)
    public IngredientResponse getIngredients(Long productId, IngredientFilterType filterType, Long memberId) {
        if (!productRepository.existsById(productId)) throw new DomainException(ProductErrorCode.INVALID_PRODUCT);
        if ((filterType == IngredientFilterType.INTEREST
                || filterType == IngredientFilterType.AVOID)
                && memberId == null) {
            throw new DomainException(AuthErrorCode.AUTH_REQUIRED);
        }

        List<IngredientItem> ingredients = productIngredientQueryRepository.findIngredients(productId, filterType, memberId);
        return new IngredientResponse((long) ingredients.size(), ingredients);
    }

    @Transactional(readOnly = true)
    public IngredientDetailResponse getIngredientDetail(Long ingredientId, Long memberId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new DomainException(IngredientErrorCode.INVALID_INGREDIENT));

        Boolean preference = resolvePreference(ingredientId, memberId);
        String extractedRaw = hazardQueryRepository.findExtractedJson(ingredientId);
        ParsedHazard parsed = parseExtracted(extractedRaw);

        return new IngredientDetailResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getLayerType(),
                ingredient.getContent(),
                preference,
                parsed.caution(),
                parsed.hazards()
        );
    }

    private Boolean resolvePreference(Long ingredientId, Long memberId) {
        if (memberId == null) return null;
        return ingredientLikeRepository
                .findByIngredientIdAndMemberId(ingredientId, memberId)
                .map(like -> like.isPreference())
                .orElse(null);
    }

    private ParsedHazard parseExtracted(String extractedRaw) {
        if (extractedRaw == null) return ParsedHazard.safe();
        try {
            JsonNode root = objectMapper.readTree(extractedRaw);
            String uiNotice = root.has("ui_notice") ? root.get("ui_notice").asText() : null;

            List<HazardItem> hazardItems = new ArrayList<>();
            if (root.has("hazard_statements") && root.get("hazard_statements").isArray()) {
                for (JsonNode node : root.get("hazard_statements")) {
                    String code = node.has("code") ? node.get("code").asText() : null;
                    String desc = node.has("description") ? node.get("description").asText() : null;

                    if (code != null) hazardItems.add(new HazardItem(code, desc));
                }
            }

            if (uiNotice == null && hazardItems.isEmpty()) return ParsedHazard.safe();
            if (hazardItems.isEmpty()) hazardItems = ParsedHazard.safe().hazards();

            return new ParsedHazard(uiNotice, hazardItems);
        } catch (Exception e) {
            log.warn("Failed to parse extracted hazard JSON: {}", extractedRaw, e);
            return ParsedHazard.safe();
        }
    }

    private record ParsedHazard(String caution, List<HazardItem> hazards) {
        static ParsedHazard safe() {
            return new ParsedHazard(
                    null,
                    List.of(new HazardItem("h코드없음(안전)", ""))
            );
        }
    }
}
