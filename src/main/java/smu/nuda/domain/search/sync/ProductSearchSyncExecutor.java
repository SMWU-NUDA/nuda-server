package smu.nuda.domain.search.sync;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.ProductQueryRepository;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.search.document.ProductDocument;
import smu.nuda.domain.search.service.SearchService;
import smu.nuda.global.cache.CacheKeyFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchSyncExecutor {

    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final SearchService searchService;
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheKeyFactory cacheKeyFactory;
    private final Clock clock;

    @Retry(name = "es-sync", fallbackMethod = "syncFallback")
    public void sync() {
        LocalDateTime since = loadLastSyncTime();
        log.info("[ES Sync] 시작 (기준 시각: {})", since);

        List<Product> changed = productRepository.findAllWithBrandUpdatedAfter(since);
        if (changed.isEmpty()) {
            log.info("[ES Sync] 변경 상품 없음, 종료");
            saveLastSyncTime(LocalDateTime.now(clock));
            return;
        }

        log.info("[ES Sync] {}개 변경 상품 발견, 인덱싱 시작", changed.size());

        List<Long> ids = changed.stream().map(Product::getId).toList();
        Map<Long, List<String>> ingredientMap = productQueryRepository.findIngredientLabelsByProductIds(ids);
        List<ProductDocument> docs = changed.stream()
                .map(p -> toDocument(p, ingredientMap.getOrDefault(p.getId(), List.of())))
                .collect(Collectors.toList());

        searchService.indexAllProducts(docs);
        saveLastSyncTime(LocalDateTime.now(clock));
        log.info("[ES Sync] {}개 인덱싱 완료", docs.size());
    }

    public void syncFallback(Exception e) {
        log.error("[ES Sync] 재시도 소진 — 수동 인덱싱 필요. 원인: {}", e.getMessage(), e);
    }

    private LocalDateTime loadLastSyncTime() {
        String value = stringRedisTemplate.opsForValue().get(cacheKeyFactory.esSyncLastTimestamp());
        if (value == null) {
            return LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception e) {
            return LocalDateTime.of(2000, 1, 1, 0, 0);
        }
    }

    private void saveLastSyncTime(LocalDateTime time) {
        stringRedisTemplate.opsForValue().set(cacheKeyFactory.esSyncLastTimestamp(), time.toString());
    }

    private ProductDocument toDocument(Product product, List<String> ingredientNames) {
        return ProductDocument.builder()
                .id(String.valueOf(product.getId()))
                .productId(product.getId())
                .productName(product.getName())
                .ingredientNames(ingredientNames)
                .brandId(product.getBrand().getId())
                .brandName(product.getBrand().getName())
                .thumbnailImg(product.getThumbnailImg())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .likeCount(product.getLikeCount())
                .costPrice(product.getCostPrice())
                .build();
    }
}
