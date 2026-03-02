package smu.nuda.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import smu.nuda.global.cache.facade.MlReviewCacheFacade;
import smu.nuda.global.ml.dto.MlReviewKeywordsResponse;
import smu.nuda.global.ml.dto.MlReviewSentimentResponse;
import smu.nuda.global.ml.dto.MlReviewTrendResponse;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ReviewAsyncService {

    private final MlReviewCacheFacade mlReviewCacheFacade;

    @Async("mlExecutor")
    public CompletableFuture<MlReviewTrendResponse> getTrendAsync(Long productId) {
        return CompletableFuture.completedFuture(mlReviewCacheFacade.getReviewTrend(productId));
    }

    @Async("mlExecutor")
    public CompletableFuture<MlReviewSentimentResponse> getSentimentAsync(Long productId) {
        return CompletableFuture.completedFuture(mlReviewCacheFacade.getReviewSentiment(productId));
    }

    @Async("mlExecutor")
    public CompletableFuture<MlReviewKeywordsResponse> getKeywordsAsync(Long productId, int topN) {
        return CompletableFuture.completedFuture(mlReviewCacheFacade.getReviewKeywords(productId, topN));
    }
}
