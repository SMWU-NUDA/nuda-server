package smu.nuda.global.cache;

import org.springframework.stereotype.Component;
import smu.nuda.domain.product.dto.enums.ProductSortType;

@Component
public class CacheKeyFactory {
    public String productDetail(Long productId) {
        return "product:detail:" + productId;
    }

    public String productSort( ProductSortType sortType, Number cursor, int size) {
        return String.format(
                "product:sort:%s:%s:%d",
                sortType.name(),
                cursor == null ? "first" : cursor.toString(),
                size
        );
    }

    public String reviewAiSummary(Long reviewId) {
        return "review:ai-summary:" + reviewId;
    }
}
