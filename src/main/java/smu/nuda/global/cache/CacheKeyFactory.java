package smu.nuda.global.cache;

import org.springframework.stereotype.Component;
import smu.nuda.domain.product.dto.enums.ProductKeywordType;
import smu.nuda.domain.product.dto.enums.ProductSortType;

@Component
public class CacheKeyFactory {
    public String productDetail(Long productId) {
        return "product:detail:" + productId;
    }

    public static String globalRanking(ProductKeywordType keyword, int topK) {
        return "ml:ranking:global:" + keyword.name() + ":top" + topK;
    }

    public String reviewAiSummary(Long reviewId) {
        return "review:ai-summary:" + reviewId;
    }
}
