package smu.nuda.domain.product.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.util.StopWatch;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.dto.ProductDetailCache;
import smu.nuda.domain.product.dto.ProductDetailResponse;
import smu.nuda.domain.product.repository.ProductImageQueryRepository;
import smu.nuda.domain.product.repository.ProductQueryRepository;
import smu.nuda.domain.like.repository.ProductLikeRepository;
import smu.nuda.support.member.MemberTestFactory;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductDetailPerformanceTest {

    @Autowired ProductService productService;
    @Autowired CacheManager cacheManager;
    @Autowired private MemberTestFactory memberTestFactory;
    @Autowired ProductQueryRepository productQueryRepository;
    @Autowired ProductImageQueryRepository productImageQueryRepository;
    @Autowired ProductLikeRepository productLikeRepository;

    private Member member;
    private Long productId;

    @BeforeAll
    void setUp() {
        member = memberTestFactory.active();
        productId = 1L;
    }

    private long measure(Runnable task, int repeat) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < repeat; i++) { task.run();}

        stopWatch.stop();
        return stopWatch.getTotalTimeMillis();
    }

    private ProductDetailResponse getProductDetailWithoutCache(Long productId, Member member) {
        ProductDetailCache base = productQueryRepository.findProductBase(productId);
        List<String> imageUrls = productImageQueryRepository.findImageUrlsByProductId(productId);

        ProductDetailCache cache = ProductDetailCache.builder()
                .productId(base.getProductId())
                .brandName(base.getBrandName())
                .name(base.getName())
                .averageRating(base.getAverageRating())
                .reviewCount(base.getReviewCount())
                .price(base.getPrice())
                .content(base.getContent())
                .imageUrls(imageUrls)
                .build();
        boolean likedByMe = productLikeRepository.existsByMember_IdAndProduct_Id(member.getId(), productId);

        return ProductDetailResponse.of(cache, likedByMe);
    }

    @Test
    @DisplayName("상품 상세 조회 성능 비교 (캐시 미적용 vs 적용)")
    void comparePerformance() {

        int repeat = 50;

        // 캐시 비우기
        System.out.println("========== 캐시 미적용 ==========");
        cacheManager.getCache("productDetail").clear();
        long withoutCache = measure(() -> getProductDetailWithoutCache(productId, member), repeat);

        // 캐시 워밍업
        System.out.println("========== 캐시 적용 ==========");
        productService.getProductDetail(productId, member);
        long withCache = measure(() -> productService.getProductDetail(productId, member), repeat);

        System.out.println("❌ 캐시 미적용 총 시간 = " + withoutCache + " ms");
        System.out.println("⭕ 캐시 적용 총 시간 = " + withCache + " ms");
        double improvement = ((double)(withoutCache - withCache) / withoutCache) * 100;
        System.out.println("📈 성능 개선율 = " + String.format("%.2f", improvement) + "%");
    }

}
