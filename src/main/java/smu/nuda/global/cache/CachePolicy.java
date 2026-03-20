package smu.nuda.global.cache;

import java.time.Duration;

public class CachePolicy {
    // 상품 상세 조회
    public static final Duration PRODUCT_DETAIL_TTL = Duration.ofMinutes(10);

    // 키워드별 전체 상품 정렬
    public static final Duration ML_PRODUCT_GLOBAL_RANKING_TTL = Duration.ofMinutes(20);

    // 키워드별 상품 맞춤 추천
    public static final Duration ML_PRODUCT_PERSONAL_RANKING_TTL = Duration.ofMinutes(7);

    // 키워드별 전체 리뷰 정렬
    public static final Duration ML_REVIEW_GLOBAL_RANKING_TTL = Duration.ofMinutes(20);

    // 리뷰 긍정/부정 키워드
    public static final Duration ML_REVIEW_KEYWORDS_TTL = Duration.ofMinutes(30);

    // 상품 리뷰 트렌드
    public static final Duration ML_REVIEW_TREND_TTL = Duration.ofMinutes(30);

    // 리뷰 사용자 만족도
    public static final Duration ML_REVIEW_SENTIMENT_TTL = Duration.ofMinutes(30);

    // 검색어 자동완성 응답 캐시
    public static final Duration SEARCH_SUGGEST_TTL = Duration.ofMinutes(5);

    // 검색어 자동완성 Rate Limit (1초)
    public static final Duration SEARCH_SUGGEST_RATE_LIMIT_TTL = Duration.ofSeconds(1);

    // 검색어 자동완성 초당 최대 요청 허용 횟수
    public static final int SEARCH_SUGGEST_MAX_REQUESTS_PER_SECOND = 5;

}
