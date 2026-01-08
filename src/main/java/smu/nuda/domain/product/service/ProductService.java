package smu.nuda.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.dto.ProductDetailCache;
import smu.nuda.domain.product.dto.ProductDetailResponse;
import smu.nuda.domain.product.repository.ProductImageQueryRepository;
import smu.nuda.domain.product.repository.ProductQueryRepository;
import smu.nuda.domain.like.repository.ProductLikeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductQueryRepository productQueryRepository;
    private final ProductImageQueryRepository productImageQueryRepository;
    private final ProductLikeRepository productLikeRepository;

    @Cacheable(value = "productDetail", key = "#productId")
    @Transactional(readOnly = true)
    public ProductDetailCache getProductDetailCache(Long productId) {

        ProductDetailCache base = productQueryRepository.findProductBase(productId);
        List<String> imageUrls = productImageQueryRepository.findImageUrlsByProductId(productId);

        return ProductDetailCache.builder()
                .productId(base.getProductId())
                .brandName(base.getBrandName())
                .name(base.getName())
                .averageRating(base.getAverageRating())
                .reviewCount(base.getReviewCount())
                .price(base.getPrice())
                .content(base.getContent())
                .imageUrls(imageUrls)
                .build();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long productId, Member member) {
        ProductDetailCache cache = getProductDetailCache(productId);
        boolean likedByMe = productLikeRepository.existsByMember_IdAndProduct_Id(member.getId(), productId);

        return ProductDetailResponse.of(cache, likedByMe);
    }

}
