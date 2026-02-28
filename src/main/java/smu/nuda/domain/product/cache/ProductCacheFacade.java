package smu.nuda.domain.product.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.product.dto.ProductDetailCache;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductImageQueryRepository;
import smu.nuda.domain.product.repository.ProductQueryRepository;
import smu.nuda.global.cache.CacheKeyFactory;
import smu.nuda.global.cache.CachePolicy;
import smu.nuda.global.cache.CacheTemplate;
import smu.nuda.global.error.DomainException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductCacheFacade {

    private final CacheTemplate cacheTemplate;
    private final CacheKeyFactory keyFactory;

    private final ProductQueryRepository productQueryRepository;
    private final ProductImageQueryRepository productImageQueryRepository;

    @Transactional(readOnly = true)
    public ProductDetailCache getProductDetail(Long productId) {
        String key = keyFactory.productDetail(productId);

        return cacheTemplate.get(
                key,
                CachePolicy.PRODUCT_DETAIL_TTL,
                () -> loadProductDetail(productId),
                ProductDetailCache.class
        );
    }

    private ProductDetailCache loadProductDetail(Long productId) {
        ProductDetailCache base = productQueryRepository.findProductBase(productId);
        if (base == null) throw new DomainException(ProductErrorCode.INVALID_PRODUCT);

        List<String> imageUrls = productImageQueryRepository.findImageUrlsByProductId(productId);

        return ProductDetailCache.builder()
                .productId(base.getProductId())
                .brandId(base.getBrandId())
                .brandName(base.getBrandName())
                .name(base.getName())
                .averageRating(base.getAverageRating())
                .reviewCount(base.getReviewCount())
                .price(base.getPrice())
                .content(base.getContent())
                .imageUrls(imageUrls)
                .build();
    }

    public void evictProductDetail(Long productId) {
        String key = keyFactory.productDetail(productId);
        cacheTemplate.evict(key);
    }
}
