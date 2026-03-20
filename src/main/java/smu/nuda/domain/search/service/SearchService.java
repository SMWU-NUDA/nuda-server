package smu.nuda.domain.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.ingredient.service.IngredientService;
import smu.nuda.domain.product.dto.ProductItem;
import smu.nuda.domain.search.document.ProductDocument;
import smu.nuda.domain.search.dto.enums.SuggestType;
import smu.nuda.domain.search.error.SearchErrorCode;
import smu.nuda.domain.search.repository.SearchKeywordRedisRepository;
import smu.nuda.domain.search.repository.SearchRepository;
import smu.nuda.global.cache.CacheKeyFactory;
import smu.nuda.global.cache.CachePolicy;
import smu.nuda.global.error.DomainException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final IngredientService ingredientService;
    private final SearchKeywordRedisRepository searchKeywordRedisRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheKeyFactory cacheKeyFactory;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    private static final int SUGGEST_LIMIT_SINGLE = 10;
    private static final int SUGGEST_LIMIT_MIXED = 5;

    @Transactional(readOnly = true)
    public CursorPageResponse<ProductItem> searchProducts(String keyword, Long cursor, int size) {
        int page = cursor == null ? 0 : cursor.intValue();
        var hits = searchRepository.searchProducts(keyword, page, size);
        int totalPages = (int) Math.ceil((double) hits.getTotalHits() / size);

        List<ProductItem> items = hits.getSearchHits().stream()
                .map(hit -> toProductItem(hit.getContent()))
                .collect(Collectors.toList());

        searchKeywordRedisRepository.increment(keyword);
        return CursorPageResponse.of(items, page + 1, totalPages);
    }

    public List<String> getPopularKeywords() {
        return searchKeywordRedisRepository.getTopKeywords(10);
    }

    public List<String> suggestKeywords(Long memberId, String keyword, SuggestType type) {
        checkRateLimit(memberId);

        String cacheKey = cacheKeyFactory.searchSuggest(type, keyword.toLowerCase());
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (Exception ignored) {}
        }

        List<String> result = switch (type) {
            case INGREDIENT -> ingredientService.suggest(keyword, SUGGEST_LIMIT_SINGLE);
            case PRODUCT -> {
                List<String> productNames;
                try {
                    productNames = searchRepository.suggestProductNames(keyword, SUGGEST_LIMIT_MIXED);
                } catch (Exception e) {
                    productNames = List.of();
                }
                List<String> ingredientNames = ingredientService.suggest(keyword, SUGGEST_LIMIT_MIXED);
                yield Stream.concat(productNames.stream(), ingredientNames.stream())
                        .distinct()
                        .collect(Collectors.toList());
            }
        };

        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey, objectMapper.writeValueAsString(result), CachePolicy.SEARCH_SUGGEST_TTL);
        } catch (Exception ignored) {}

        return result;
    }

    public void indexProduct(ProductDocument doc) {
        searchRepository.indexProduct(doc);
    }

    public void indexAllProducts(List<ProductDocument> docs) {
        searchRepository.indexAllProducts(docs);
    }

    private void checkRateLimit(Long memberId) {
        long epochSecond = Instant.now(clock).getEpochSecond();
        String key = cacheKeyFactory.searchSuggestRateLimit(memberId, epochSecond);
        Long count = stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, CachePolicy.SEARCH_SUGGEST_RATE_LIMIT_TTL);
        if (count != null && count > CachePolicy.SEARCH_SUGGEST_MAX_REQUESTS_PER_SECOND) {
            throw new DomainException(SearchErrorCode.SUGGEST_RATE_LIMIT_EXCEEDED);
        }
    }

    private ProductItem toProductItem(ProductDocument doc) {
        ProductItem item = new ProductItem(
                doc.getProductId(),
                doc.getThumbnailImg(),
                doc.getBrandId(),
                doc.getBrandName(),
                doc.getProductName(),
                doc.getAverageRating(),
                doc.getReviewCount(),
                doc.getLikeCount(),
                doc.getCostPrice()
        );
        item.setIngredientLabels(doc.getIngredientNames());
        return item;
    }
}
