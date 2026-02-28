package smu.nuda.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.review.dto.MyReviewResponse;
import smu.nuda.domain.review.dto.ReviewCreateRequest;
import smu.nuda.domain.review.dto.ReviewDetailResponse;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.entity.ReviewImage;
import smu.nuda.domain.review.event.ReviewUpdateEvent;
import smu.nuda.domain.review.repository.ReviewImageRepository;
import smu.nuda.domain.review.repository.ReviewQueryRepository;
import smu.nuda.domain.review.repository.ReviewRepository;
import smu.nuda.global.error.DomainException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReviewDetailResponse create(ReviewCreateRequest request, Member member) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new DomainException(ProductErrorCode.INVALID_PRODUCT));

        Review review = new Review(
                member,
                product,
                request.getRating(),
                request.getContent(),
                null
        );
        reviewRepository.save(review);

        // 상품 상세조회 캐시 삭제 이벤트 발행
        eventPublisher.publishEvent(new ReviewUpdateEvent(product.getId()));

        List<String> imageUrls = request.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            saveImages(review, imageUrls);
        }

        return ReviewDetailResponse.of(review, imageUrls, false);
    }

    private void saveImages(Review review, List<String> imageUrls) {
        List<ReviewImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            images.add(new ReviewImage(
                    review,
                    imageUrls.get(i),
                    i
            ));
        }
        reviewImageRepository.saveAll(images);
        if (!imageUrls.isEmpty()) review.updateThumbnail(imageUrls.get(0));
    }

    @Transactional
    public void delete(Review review) {
        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);

        // 상품 상세조회 캐시 삭제 이벤트 발행
        eventPublisher.publishEvent(new ReviewUpdateEvent(productId));
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<MyReviewResponse> getMyReviews(Long memberId, Long cursor, int size) {
        List<MyReviewResponse> result = reviewQueryRepository.findMyReviews(memberId, cursor, size);

        return CursorPageResponse.of(
                result,
                size,
                MyReviewResponse::getReviewId
        );
    }
}
