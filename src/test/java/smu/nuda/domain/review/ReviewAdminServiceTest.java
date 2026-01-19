package smu.nuda.domain.review;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.brand.repository.BrandRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.domain.product.entity.Category;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.entity.enums.CategoryCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.review.csv.ReviewCsvReader;
import smu.nuda.domain.review.dto.ReviewCsvRow;
import smu.nuda.domain.review.repository.ReviewRepository;
import smu.nuda.domain.review.service.ReviewAdminService;
import smu.nuda.domain.review.validator.ReviewCsvValidator;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;
import smu.nuda.support.category.CategoryTestFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
    리뷰 CSV 일괄 업로드 기능을 검증하는 서비스 테스트

    - 관리자 전용 배치 업로드 기능
    - 리뷰 평점(rating) 등 필수 필드 유효성 검증
    - CSV 데이터와 실제 DB 상품(Product) 간의 매핑 및 저장 확인
    - 업로드 성공 시 리뷰 엔티티 생성 및 상품 정보 정합성 검증
*/

@SpringBootTest
public class ReviewAdminServiceTest {
    @Autowired private ReviewCsvValidator reviewCsvValidator;
    @Autowired private ReviewCsvReader reviewCsvReader;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CategoryTestFactory categoryTestFactory;
    @Autowired private ReviewAdminService reviewAdminService;
    @Autowired private ProductRepository productRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("정상 리뷰 CSV는 검증을 통과한다")
    void validate_success_withCsvFile() throws Exception {
        // [given] 정상 형식의 리뷰 CSV 파일
        MockMultipartFile csvFile = loadCsv("reviews_valid.csv");
        List<ReviewCsvRow> rows = reviewCsvReader.read(csvFile);

        // [when, then] 모든 리뷰가 성공 처리됨
        assertDoesNotThrow(() -> reviewCsvValidator.validate(rows));
    }

    @Test
    @DisplayName("rating이 숫자가 아니면 FAIL")
    void validate_fail_whenRatingInvalid() throws Exception {
        // [given] 평점 데이터가 숫자가 아닌 형식이 포함된 리뷰 CSV 파일
        MockMultipartFile csvFile = loadCsv("reviews_invalid_rating.csv");
        List<ReviewCsvRow> rows = reviewCsvReader.read(csvFile);

        // [when, then] CSV 검증 예외가 발생하고 에러 메시지에서 CSV_INVALID_VALUE 에러코드를 포함함
        CsvValidationException e = assertThrows(
                CsvValidationException.class,
                () -> reviewCsvValidator.validate(rows)
        );

        assertThat(e.getErrorCode())
                .isEqualTo(CsvErrorCode.CSV_INVALID_VALUE);
    }

    @Test
    @DisplayName("리뷰 CSV 업로드 성공 시 리뷰가 저장된다")
    void uploadReviews_success_withCsv() throws Exception {
        // [given] 리뷰와 매핑될 관리자, 브랜드, 카테고리 및 대상 상품(P-001)을 DB에 저장
        Member csvAdmin = memberRepository.findByUsername("csvAdmin").orElseThrow();
        Brand brand = brandRepository.save(
                Brand.builder()
                        .name("BRAND_A")
                        .build()
        );
        Category category = categoryTestFactory.getOrCreate(CategoryCode.SMALL);

        Product product = productRepository.save(
                Product.create(
                        "P-001",
                        brand,
                        category,
                        "상품",
                        1000,
                        10,
                        null,
                        null,
                        0,
                        0
                )
        );

        MockMultipartFile csvFile = loadCsv("reviews_valid.csv");

        // [when] 실제 업로드를 dryRun = false로 실행
        reviewAdminService.uploadReviewsByCsv(csvFile, false);

        // [then] 리뷰 레포지토리에 데이터가 1건 저장됨
        assertThat(reviewRepository.count()).isEqualTo(1);

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updated.getReviewCount()).isEqualTo(0);
        assertThat(updated.getAverageRating()).isEqualTo(0);
    }

    protected MockMultipartFile loadCsv(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource("csv/review/" + path);

        return new MockMultipartFile(
                "file",
                resource.getFilename(),
                "text/csv",
                resource.getInputStream()
        );
    }

}
