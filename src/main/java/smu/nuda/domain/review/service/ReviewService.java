package smu.nuda.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.like.repository.ReviewLikeRepository;
import smu.nuda.domain.member.dto.MeResponse;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.review.dto.MyReviewResponse;
import smu.nuda.domain.review.dto.ReviewCreateRequest;
import smu.nuda.domain.review.dto.ReviewItem;
import smu.nuda.domain.review.dto.enums.ReviewKeywordType;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.entity.ReviewImage;
import smu.nuda.domain.review.event.ReviewUpdateEvent;
import smu.nuda.domain.review.repository.ReviewImageRepository;
import smu.nuda.domain.review.repository.ReviewQueryRepository;
import smu.nuda.domain.review.repository.ReviewRepository;
import smu.nuda.domain.review.repository.projection.ReviewRankingProjection;
import smu.nuda.global.cache.facade.MlReviewCacheFacade;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.ml.exception.MlApiException;
import smu.nuda.global.util.DateFormatUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private static final int DEFAULT_SIZE = 20;

    private final MlReviewCacheFacade mlReviewCacheFacade;
    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReviewItem create(ReviewCreateRequest request, Member member) {

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

        return ReviewItem.of(review, imageUrls, false);
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

    @Transactional(readOnly = true)
    public CursorPageResponse<ReviewItem> getGlobalRankingPage(Long productId, Long memberId, ReviewKeywordType keyword, Long cursor, Integer size) {
        final int topK = 300;
        List<Integer> rankedIds;

        try {
            rankedIds = mlReviewCacheFacade.getGlobalReviewRanking(productId, keyword);
        } catch (MlApiException e) {
            log.warn("[ReviewFallback] ML 실패 → DB fallback 사용. productId={}", productId);

            List<Long> fallbackIds = reviewRepository.findFallbackTopIds(productId, PageRequest.of(0, topK));
            rankedIds = fallbackIds.stream()
                    .map(Long::intValue)
                    .toList();
        }

        return getReviewRankingPageFromIds(rankedIds, memberId, cursor, size);
    }

    private CursorPageResponse<ReviewItem> getReviewRankingPageFromIds(List<Integer> rankedIds, Long memberId, Long cursor, Integer size) {
        int pageSize = size == null ? DEFAULT_SIZE : size;

        if (rankedIds == null || rankedIds.isEmpty()) return new CursorPageResponse<>(List.of(), null, false);

        // index 기반 slice
        CursorPageResponse<Integer> indexPage = CursorPageResponse.sliceFromIndex(rankedIds, cursor, pageSize);
        if (indexPage.getContent().isEmpty()) return new CursorPageResponse<>(List.of(), null, false);
        List<Long> pageIds = indexPage.getContent().stream().map(Integer::longValue).toList();

        // 최적화를 위해 index map 생성 -> O(n)
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < pageIds.size(); i++) {
            orderMap.put(pageIds.get(i), i);
        }

        // DB 조회 후 ml 순서대로 정렬 -> O(n)
        List<ReviewRankingProjection> projections = reviewRepository.findRankingReviews(pageIds);
        List<ReviewRankingProjection> sortedReviews = projections.stream()
                .sorted(Comparator.comparingInt(r -> orderMap.get(r.getReviewId())))
                .toList();

        // 이미지 일괄 조회
        List<Object[]> imageRows = reviewImageRepository.findImages(pageIds);
        Map<Long, List<String>> imageMap = imageRows
                .stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));

        // 좋아요 여부 조회
        Set<Long> likedReviewIds = reviewLikeRepository.findLikedReviewIds(memberId, pageIds);

        // DTO 조립
        List<ReviewItem> result = sortedReviews.stream()
                .map(r -> ReviewItem.builder()
                                .reviewId(r.getReviewId())
                                .productId(r.getProductId())
                                .me(MeResponse.of(
                                        r.getMemberId(),
                                        r.getMemberUsername(),
                                        r.getMemberNickname(),
                                        r.getMemberProfileImg(),
                                        r.getMemberEmail()
                                ))
                                .rating(r.getRating())
                                .likeCount(r.getLikeCount())
                                .likedByMe(likedReviewIds.contains(r.getReviewId()))
                                .content(r.getContent())
                                .imageUrls(imageMap.getOrDefault(r.getReviewId(), List.of()))
                                .createdAt(DateFormatUtil.formatDate(r.getCreatedAt()))
                                .build()
                        )
                        .toList();

        return new CursorPageResponse<>(
                result,
                indexPage.getNextCursor(),
                indexPage.isHasNext()
        );
    }
}
