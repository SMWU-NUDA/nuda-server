package smu.nuda.domain.search.sync;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.ProductQueryRepository;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.search.document.ProductDocument;
import smu.nuda.domain.search.service.ProductSearchService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchSyncExecutor {

    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ProductSearchService productSearchService;
    private final ElasticsearchOperations elasticsearchOperations;

    @Retry(name = "es-sync", fallbackMethod = "syncFallback")
    public void sync() {
        log.info("[ES Sync] 시작");

        List<Long> allDbIds = productRepository.findAllIds();
        if (allDbIds.isEmpty()) {
            log.info("[ES Sync] DB에 상품 없음, 종료");
            return;
        }

        Set<String> indexedIds = fetchAllIndexedIds();

        List<Long> missingIds = allDbIds.stream()
                .filter(id -> !indexedIds.contains(String.valueOf(id)))
                .collect(Collectors.toList());

        if (missingIds.isEmpty()) {
            log.info("[ES Sync] 동기화 불필요 ({}개 모두 인덱싱됨)", allDbIds.size());
            return;
        }

        log.info("[ES Sync] {}개 미인덱싱 상품 발견, 인덱싱 시작", missingIds.size());

        List<Product> products = productRepository.findAllWithBrandByIdIn(missingIds);
        Map<Long, List<String>> ingredientMap = productQueryRepository.findIngredientLabelsByProductIds(missingIds);
        List<ProductDocument> docs = products.stream()
                .map(p -> toDocument(p, ingredientMap.getOrDefault(p.getId(), List.of())))
                .collect(Collectors.toList());

        productSearchService.indexAll(docs);
        log.info("[ES Sync] {}개 인덱싱 완료", docs.size());
    }

    public void syncFallback(Exception e) {
        log.error("[ES Sync] 재시도 소진 — 수동 인덱싱 필요. 원인: {}", e.getMessage(), e);
    }

    private Set<String> fetchAllIndexedIds() {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.matchAll(m -> m))
                .build();

        Set<String> ids = new java.util.HashSet<>();
        try (SearchHitsIterator<ProductDocument> stream =
                     elasticsearchOperations.searchForStream(query, ProductDocument.class)) {
            stream.forEachRemaining(hit -> ids.add(hit.getId()));
        }
        return ids;
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