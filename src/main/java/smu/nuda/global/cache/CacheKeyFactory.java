package smu.nuda.global.cache;

import org.springframework.stereotype.Component;
import smu.nuda.domain.product.dto.enums.ProductKeywordType;
import smu.nuda.domain.review.dto.enums.ReviewKeywordType;

@Component
public class CacheKeyFactory {
    public String productDetail(Long productId) {
        return "product:detail:" + productId;
    }

    public String productGlobalRanking(ProductKeywordType keyword, int topK) {
        return "ml:product:ranking:global:" + keyword.name() + ":top" + topK;
    }

    public String productPersonalRanking(Long memberId, ProductKeywordType keyword, int topK) {
        return "ml:product:ranking:personal:" + memberId + ":" + keyword.name() + ":top" + topK;
    }

    public String reviewGlobalRanking(Long productId, ReviewKeywordType keyword, int topK) {
        return "ml:review:ranking:global:" + productId + ":" + keyword.name() + ":top" + topK;
    }

    public String reviewKeywords(Long productId, int topN) {
        return "ml:review:keywords:" + productId + ":top" + topN;
    }

    public String reviewTrend(Long productId) {
        return "ml:review:trend:" + productId;
    }

    public String reviewSentiment(Long productId) {
        return "ml:review:sentiment:" + productId;
    }

    public String searchKeywordRanking(String weekKey) {
        return "search:keyword:ranking:" + weekKey;
    }
}
