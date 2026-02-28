package smu.nuda.global.cache;

import java.time.Duration;

public class CachePolicy {
    // 상품 상세 조회
    public static final Duration PRODUCT_DETAIL_TTL = Duration.ofMinutes(10);

    // 키워드별 전체 상품 정렬
    public static final Duration KEYWORD_SORTED_PRODUCTS_TTL = Duration.ofMinutes(3);

    // 키워드별 상품 맞춤 추천 조회
    public static final Duration ML_RECOMMENDED_PRODUCTS_TTL = Duration.ofMinutes(3);

}
