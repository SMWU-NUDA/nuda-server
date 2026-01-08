package smu.nuda.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.brand.error.BrandErrorCode;
import smu.nuda.domain.brand.repository.BrandRepository;
import smu.nuda.domain.like.dto.LikeToggleResponse;
import smu.nuda.domain.like.entity.BrandLike;
import smu.nuda.domain.like.entity.ProductLike;
import smu.nuda.domain.like.repository.BrandLikeRepository;
import smu.nuda.domain.like.repository.ProductLikeRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.error.DomainException;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final BrandRepository brandRepository;
    private final BrandLikeRepository brandLikeRepository;

    public LikeToggleResponse productLikeToggle(Long productId, Member member) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DomainException(ProductErrorCode.INVALID_PRODUCT));

        return productLikeRepository
                .findByMemberAndProduct(member, product)
                .map(like -> {
                    // 이미 찜 → 취소
                    productLikeRepository.delete(like);
                    product.decreaseLikeCount();
                    return LikeToggleResponse.unliked(product.getLikeCount());
                })
                .orElseGet(() -> {
                    // 찜 안됨 → 찜
                    productLikeRepository.save(new ProductLike(member, product));
                    product.increaseLikeCount();
                    return LikeToggleResponse.liked(product.getLikeCount());
                });
    }

    public LikeToggleResponse brandLikeToggle(Long brandId, Member member) {

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new DomainException(BrandErrorCode.INVALID_BRAND));

        return brandLikeRepository
                .findByMemberAndBrand(member, brand)
                .map(like -> {
                    // 이미 찜 → 취소
                    brandLikeRepository.delete(like);
                    brand.decreaseLikeCount();
                    return LikeToggleResponse.unliked(brand.getLikeCount());
                })
                .orElseGet(() -> {
                    // 찜 안됨 → 찜
                    brandLikeRepository.save(new BrandLike(member, brand));
                    brand.increaseLikeCount();
                    return LikeToggleResponse.liked(brand.getLikeCount());
                });
    }

}
