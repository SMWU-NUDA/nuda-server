package smu.nuda.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.review.dto.ReviewCreateRequest;
import smu.nuda.domain.review.dto.ReviewDetailResponse;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.entity.ReviewImage;
import smu.nuda.domain.review.repository.ReviewImageRepository;
import smu.nuda.domain.review.repository.ReviewRepository;
import smu.nuda.global.error.DomainException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ReviewDetailResponse create(ReviewCreateRequest request, Member member) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new DomainException(ProductErrorCode.INVALID_PRODUCT));

        Review review = new Review(
                member,
                product,
                request.getRating(),
                request.getPros(),
                request.getCons(),
                null
        );
        reviewRepository.save(review);

        List<String> imageUrls = request.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            saveImages(review, imageUrls);
        }

        return ReviewDetailResponse.of(review, imageUrls, false);
    }

    private void saveImages(Review review, List<String> imageUrls) {

        for (int i = 0; i < imageUrls.size(); i++) {
            ReviewImage image = new ReviewImage(
                    review,
                    imageUrls.get(i),
                    i
            );
            reviewImageRepository.save(image);
            if (i == 0) review.updateThumbnail(imageUrls.get(i));
        }
    }

}
