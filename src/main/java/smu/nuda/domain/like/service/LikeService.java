package smu.nuda.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.brand.error.BrandErrorCode;
import smu.nuda.domain.brand.repository.BrandRepository;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.ingredient.entity.Ingredient;
import smu.nuda.domain.ingredient.error.IngredientErrorCode;
import smu.nuda.domain.ingredient.repository.IngredientRepository;
import smu.nuda.domain.like.dto.*;
import smu.nuda.domain.like.entity.BrandLike;
import smu.nuda.domain.like.entity.IngredientLike;
import smu.nuda.domain.like.entity.ProductLike;
import smu.nuda.domain.like.entity.ReviewLike;
import smu.nuda.domain.like.repository.*;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.error.ReviewErrorCode;
import smu.nuda.domain.review.repository.ReviewRepository;
import smu.nuda.global.error.DomainException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ProductLikeQueryRepository productLikeQueryRepository;
    private final BrandRepository brandRepository;
    private final BrandLikeRepository brandLikeRepository;
    private final BrandLikeQueryRepository brandLikeQueryRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientLikeRepository ingredientLikeRepository;
    private final IngredientLikeQueryRepository ingredientLikeQueryRepository;

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

    @Transactional
    public LikeToggleResponse reviewLikeToggle(Long reviewId, Member member) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new DomainException(ReviewErrorCode.INVALID_REVIEW));

        return reviewLikeRepository
                .findByMemberAndReview(member, review)
                .map(like -> {
                    // 이미 좋아요 → 취소
                    reviewLikeRepository.delete(like);
                    review.decreaseLike();
                    return LikeToggleResponse.unliked(review.getLikeCount());
                })
                .orElseGet(() -> {
                    // 좋아요 안됨 → 좋아요
                    reviewLikeRepository.save(new ReviewLike(review, member));
                    review.increaseLike();
                    return LikeToggleResponse.liked(review.getLikeCount());
                });
    }

    @Transactional
    public PreferToggleResponse ingredientPreferToggle(Long ingredientId, boolean preference, Member member) {

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new DomainException(IngredientErrorCode.INVALID_INGREDIENT));

        return ingredientLikeRepository
                .findByMemberAndIngredient(member, ingredient)
                .map(existing -> {
                    // 같은 버튼 다시 누르면 → 삭제
                    if (existing.isPreference() == preference) {
                        ingredientLikeRepository.delete(existing);
                        return PreferToggleResponse.none();
                    }
                    // 반대 버튼 누르면 → 변경
                    existing.updatePreference(preference);

                    return preference
                            ? PreferToggleResponse.interested()
                            : PreferToggleResponse.avoided();
                })
                .orElseGet(() -> {
                    // 기존 데이터 없으면 새로 생성
                    IngredientLike newLike = preference ? IngredientLike.prefer(member, ingredient) : IngredientLike.avoid(member, ingredient);
                    ingredientLikeRepository.save(newLike);

                    return preference
                            ? PreferToggleResponse.interested()
                            : PreferToggleResponse.avoided();
                });
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<LikedProductResponse> likedProducts(Member member, Long cursor, int size) {
        List<LikedProductResponse> result = productLikeQueryRepository.findLikedProducts(member.getId(), cursor, size);

        return CursorPageResponse.of(
                result,
                size,
                LikedProductResponse::getLikeId
        );
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<LikedBrandResponse> likedBrands(Member member, Long cursor, int size) {
        List<LikedBrandResponse> result = brandLikeQueryRepository.findLikedBrands(member.getId(), cursor, size);

        return CursorPageResponse.of(
                result,
                size,
                LikedBrandResponse::getLikeId
        );
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<LikedIngredientResponse> likedIngredients(Member member, Boolean preference, Long cursor, int size) {
        List<LikedIngredientResponse> result = ingredientLikeQueryRepository
                .findLikedIngredients(member.getId(), preference, cursor, size);

        return CursorPageResponse.of(
                result,
                size,
                LikedIngredientResponse::getLikeId
        );
    }

}
