package smu.nuda.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import smu.nuda.global.cache.facade.MlReviewCacheFacade;
import smu.nuda.global.error.MlErrorCode;
import smu.nuda.global.ml.dto.MlReviewKeywordsResponse;
import smu.nuda.global.ml.dto.MlReviewSentimentResponse;
import smu.nuda.global.ml.dto.MlReviewTrendResponse;
import smu.nuda.global.ml.exception.MlApiException;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewAsyncService {

    private final MlReviewCacheFacade mlReviewCacheFacade;

    @Async("mlExecutor")
    public CompletableFuture<MlReviewTrendResponse> getTrendAsync(Long productId) {
        try {
            return CompletableFuture.completedFuture(mlReviewCacheFacade.getReviewTrend(productId));
        } catch (MlApiException e) {
            // 리뷰 부족 비즈니스 예외
            if (e.getErrorCode() == MlErrorCode.REVIEW_INSUFFICIENT) return CompletableFuture.failedFuture(e);
            log.warn("[ML PartialFailure] Trend failed productId={}", productId, e);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.warn("[ML PartialFailure] Trend failed productId={}", productId, e);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Async("mlExecutor")
    public CompletableFuture<MlReviewSentimentResponse> getSentimentAsync(Long productId) {
        try {
            return CompletableFuture.completedFuture(mlReviewCacheFacade.getReviewSentiment(productId));
        } catch (MlApiException e) {
            // 리뷰 부족 비즈니스 예외
            if (e.getErrorCode() == MlErrorCode.REVIEW_INSUFFICIENT) return CompletableFuture.failedFuture(e);
            log.warn("[ML PartialFailure] Trend failed productId={}", productId, e);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.warn("[ML PartialFailure] Sentiment failed productId={}", productId, e);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Async("mlExecutor")
    public CompletableFuture<MlReviewKeywordsResponse> getKeywordsAsync(Long productId, int topN) {
        try {
            return CompletableFuture.completedFuture(mlReviewCacheFacade.getReviewKeywords(productId, topN));
        } catch (MlApiException e) {
            // 리뷰 부족 비즈니스 예외
            if (e.getErrorCode() == MlErrorCode.REVIEW_INSUFFICIENT) return CompletableFuture.failedFuture(e);
            log.warn("[ML PartialFailure] Trend failed productId={}", productId, e);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.warn("[ML PartialFailure] Keywords failed productId={}", productId, e);
            return CompletableFuture.completedFuture(null);
        }
    }
}
