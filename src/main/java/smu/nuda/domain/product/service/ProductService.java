package smu.nuda.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.common.dto.Cursor;
import smu.nuda.domain.common.dto.CursorResponse;
import smu.nuda.domain.like.repository.BrandLikeRepository;
import smu.nuda.domain.product.cache.ProductCacheFacade;
import smu.nuda.domain.product.dto.ProductDetailCache;
import smu.nuda.domain.product.dto.ProductDetailResponse;
import smu.nuda.domain.product.dto.ProductItem;
import smu.nuda.domain.product.dto.enums.ProductSortType;
import smu.nuda.domain.product.repository.ProductQueryRepository;
import smu.nuda.domain.like.repository.ProductLikeRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCacheFacade productCacheFacade;
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
        return productQueryRepository.findProductCursorPage(sortType, cursor, size);
    }

}
