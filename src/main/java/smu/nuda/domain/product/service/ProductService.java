package smu.nuda.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.common.dto.Cursor;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.common.dto.CursorResponse;
import smu.nuda.domain.like.repository.BrandLikeRepository;
import smu.nuda.domain.product.cache.ProductCacheFacade;
import smu.nuda.domain.product.dto.ProductDetailCache;
import smu.nuda.domain.product.dto.ProductDetailResponse;
import smu.nuda.domain.product.dto.ProductItem;
import smu.nuda.domain.product.dto.enums.ProductKeywordType;
import smu.nuda.domain.product.dto.enums.ProductSortType;
import smu.nuda.domain.product.repository.ProductQueryRepository;
import smu.nuda.domain.like.repository.ProductLikeRepository;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.product.repository.projection.ProductRankingProjection;
import smu.nuda.global.cache.facade.MlProductCacheFacade;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int DEFAULT_SIZE = 20;

    private final ProductCacheFacade productCacheFacade;
    private final MlProductCacheFacade mlProductCacheFacade;
    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ProductLikeRepository productLikeRepository;
    private final BrandLikeRepository brandLikeRepository;

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long productId, Long memberId) {
        ProductDetailCache cache = productCacheFacade.getProductDetail(productId);

        boolean productLikedByMe = productLikeRepository.existsByMember_IdAndProduct_Id(memberId, productId);
        boolean brandLikedByMe = brandLikeRepository.existsByMember_IdAndBrand_Id(memberId, cache.getBrandId());

        return ProductDetailResponse.of(cache, productLikedByMe, brandLikedByMe);
    }

    @Transactional(readOnly = true)
    public CursorResponse<ProductItem, Number> getSortedProducts(ProductSortType sortType, Cursor<Number> cursor, int size) {
        CursorResponse<ProductItem, Number> response = productQueryRepository.findProductCursorPage(sortType, cursor, size);

        // 성분 미리보기 내용
        List<ProductItem> items = response.getContent();
        List<Long> productIds = items.stream()
                .map(ProductItem::getProductId)
                .toList();
        Map<Long, List<String>> ingredientMap = productQueryRepository.findIngredientLabelsByProductIds(productIds);

        for (ProductItem item : items) {
            item.setIngredientLabels(ingredientMap.getOrDefault(item.getProductId(), List.of()));
        }

        return response;
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<ProductItem> getGlobalRankingPage(ProductKeywordType keyword, Long cursor, Integer size) {
        List<Integer> rankedIds = mlProductCacheFacade.getGlobalRanking(keyword);
        return getRankingPageFromIds(rankedIds, cursor, size);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<ProductItem> getPersonalRankingPage(Long memberId, ProductKeywordType keyword, Long cursor, Integer size) {
        List<Integer> rankedIds = mlProductCacheFacade.getPersonalRanking(memberId, keyword);
        return getRankingPageFromIds(rankedIds, cursor, size);
    }

    private CursorPageResponse<ProductItem> getRankingPageFromIds(List<Integer> rankedIds, Long cursor, Integer size) {
        int pageSize = size == null ? DEFAULT_SIZE : size;

        if (rankedIds == null || rankedIds.isEmpty()) return new CursorPageResponse<>(List.of(), null, false);

        // index 기반 slice
        CursorPageResponse<Integer> indexPage = CursorPageResponse.sliceFromIndex(rankedIds, cursor, pageSize);
        if (indexPage.getContent().isEmpty()) return new CursorPageResponse<>(List.of(), null, false);
        List<Long> pageIds = indexPage.getContent().stream().map(Integer::longValue) .toList();

        // 최적화를 위해 index map 생성 -> O(n)
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < pageIds.size(); i++) {
            orderMap.put(pageIds.get(i), i);
        }
        // DB 조회 후 ml 순서대로 정렬 -> O(n)
        List<ProductRankingProjection> projections = productRepository.findRankingProducts(pageIds)
                .stream()
                .sorted(Comparator.comparingInt(p -> orderMap.get(p.getProductId())))
                .toList();
        Map<Long, List<String>> labelMap = productQueryRepository.findIngredientLabelsByProductIds(pageIds);

        List<ProductItem> result = projections.stream()
                .map(p -> {
                    ProductItem item = new ProductItem(
                            p.getProductId(),
                            p.getThumbnailImg(),
                            p.getBrandId(),
                            p.getBrandName(),
                            p.getProductName(),
                            p.getAverageRating(),
                            p.getReviewCount(),
                            p.getLikeCount(),
                            p.getCostPrice()
                    );
                    item.setIngredientLabels(labelMap.getOrDefault(p.getProductId(), List.of()));
                    return item;
                })
                .toList();

        return new CursorPageResponse<>(
                result,
                indexPage.getNextCursor(),
                indexPage.isHasNext()
        );
    }

}
