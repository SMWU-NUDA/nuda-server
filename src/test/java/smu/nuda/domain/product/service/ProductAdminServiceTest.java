package smu.nuda.domain.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.brand.repository.BrandRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.common.dto.CsvUploadResponse;
import smu.nuda.domain.product.entity.Category;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.entity.enums.CategoryCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;
import smu.nuda.global.error.DomainException;
import smu.nuda.support.category.CategoryTestFactory;
import smu.nuda.support.member.MemberTestFactory;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ProductAdminServiceTest {

    /*
    상품 CSV 일괄 업로드 기능을 검증하는 서비스 테스트

    - 관리자 전용 배치 업로드 기능
    - dry-run / 실제 업로드 동작 차이 검증
    - CSV 검증 오류와 도메인 규칙 위반 예외 분리
    - 에러 메시지(message)와 상세 정보(data)의 책임 분리 검증
    */

    @Autowired private ProductAdminService productAdminService;
    @Autowired private ProductRepository productRepository;
    @Autowired private MemberTestFactory memberTestFactory;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CategoryTestFactory categoryTestFactory;

    private Member admin;
    private Brand brand;
    private Category category;

    @BeforeEach
    void setUp() {
        admin = memberTestFactory.admin();
        brand = brandRepository.save(
                Brand.builder()
                        .name("BRAND_A")
                        .build()
        );
        category = categoryTestFactory.getOrCreate(CategoryCode.SMALL);
    }

    @Test
    @DisplayName("dry-run 모드에서는 상품이 DB에 저장되지 않는다")
    void dryRun_doesNotPersistProducts() throws Exception {
        // [given] 정상 형식의 상품 CSV 파일
        MockMultipartFile csvFile = loadCsv("products_valid.csv");

        // [when] dry-run 모드로 CSV 업로드 요청
        CsvUploadResponse response = productAdminService.uploadProductsByCsv(csvFile, true);

        // [then] 모든 상품이 성공 처리되지만 실제 DB에는 seed 데이터만 존재함
        assertThat(response.totalCount()).isEqualTo(2000);
        assertThat(response.successCount()).isEqualTo(0);
        assertThat(response.failedCount()).isEqualTo(0);
        assertThat(productRepository.count()).isEqualTo(5); // seed_product
    }

    @Test
    @DisplayName("실제 업로드 시 상품이 DB에 저장된다")
    void realUpload_persistsProducts() throws Exception {
        // [given] 정상 형식의 상품 CSV 파일
        MockMultipartFile csvFile = loadCsv("products_valid.csv");

        // [when] 실제 업로드 모드로 CSV 업로드 요청
        CsvUploadResponse response = productAdminService.uploadProductsByCsv(csvFile, false);

        // [then] 모든 상품이 성공 처리되고 DB에는 seed 데이터와 신규 상품이 저장됨
        assertThat(response.successCount()).isEqualTo(2000);
        assertThat(productRepository.count()).isEqualTo(2000 + 5); // seed_product
    }

    @Test
    @DisplayName("존재하지 않는 브랜드가 있으면 CSV 검증 에러 발생")
    void invalidBrand_throwsCsvValidationException() throws Exception {
        // [given] 존재하지 않는 브랜드를 참조하는 CSV 파일
        MockMultipartFile csvFile = loadCsv("products_invalid_brand.csv");

        // [when] dry-run 모드로 CSV 업로드 요청
        Throwable throwable = catchThrowable(() -> productAdminService.uploadProductsByCsv(csvFile, true));

        // [then] CSV 검증 예외가 발생하고
        assertThat(throwable).isInstanceOf(CsvValidationException.class);
        CsvValidationException ex = (CsvValidationException) throwable;
        // 공통 메시지는 참조 대상 오류이며
        assertThat(ex.getMessage()).isEqualTo("참조 대상이 존재하지 않습니다.");
        // 상세 정보에는 오류가 발생한 CSV 줄과 원인이 포함됨
        assertThat(ex.getData()).isNotNull();
        assertThat(ex.getData().toString())
                .contains("CSV 1번째 줄")
                .contains("존재하지 않는 브랜드");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리면 CSV 에러 발생")
    void invalidCategory_throwsException() throws Exception {
        // [given] 존재하지 않는 카테고리를 참조하는 CSV 파일
        MockMultipartFile csvFile = loadCsv("products_invalid_category.csv");

        // [when] dry-run 모드로 CSV 업로드 요청
        Throwable throwable = catchThrowable(() -> productAdminService.uploadProductsByCsv(csvFile, true));

        // [then] CSV 검증 예외가 발생하고 참조 대상 오류와 관련 칼럼 정보가 포함됨
        assertThat(throwable).isInstanceOf(CsvValidationException.class);
        CsvValidationException ex = (CsvValidationException) throwable;
        assertThat(ex.getMessage()).isEqualTo("참조 대상이 존재하지 않습니다.");
        assertThat(ex.getData().toString())
                .contains("CSV")
                .contains("카테고리");
    }


    @Test
    @DisplayName("할인율 범위 초과 시 도메인 예외 발생")
    void invalidDiscount_throwsDomainException() throws Exception {
        // [given] 할인율이 허용 범위를 초과한 CSV 파일
        MockMultipartFile csvFile = loadCsv("products_invalid_discount.csv");

        // [when, then] 업로드 요청 시 도메인 규칙 위반 예외 발생
        assertThatThrownBy(() -> productAdminService.uploadProductsByCsv(csvFile, true))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("할인율");
    }

    @Test
    @DisplayName("상품명이 비어 있으면 CSV 검증 실패")
    void missingName_throwsException() throws Exception {
        // [given] 필수 컬럼(name)이 누락된 CSV 파일
        MockMultipartFile csvFile = loadCsv("products_missing_field.csv");

        // [when] dry-run 모드로 CSV 업로드 요청
        Throwable throwable = catchThrowable(() -> productAdminService.uploadProductsByCsv(csvFile, true));

        // [then] CSV 검증 예외가 발생하고 필수 칼럼 누락 오류와 관련 칼럼 정보가 포함됨
        assertThat(throwable).isInstanceOf(CsvValidationException.class);
        CsvValidationException ex = (CsvValidationException) throwable;
        assertThat(ex.getMessage()).isEqualTo("필수 컬럼이 누락되었습니다.");
        assertThat(ex.getData().toString())
                .contains("CSV")
                .contains("name");
    }

    @Test
    @DisplayName("DB에 이미 존재하는 external_product_id가 있으면 업로드는 FAIL 한다")
    void uploadProducts_fail_whenExternalProductIdAlreadyExists() throws Exception {
        // [given] DB에 중복 대상이 되는 기존 상품을 1개 저장
        Product existing = productRepository.save(
                Product.create(
                        "DUPLICATE-001",
                        brand,
                        category,
                        "기존 상품",
                        10000,
                        10,
                        "기존 상품 설명",
                        null,
                        4.5,
                        10
                )
        );

        long beforeCount = productRepository.count();

        // [given] 이미 DB에 저장된 external_product_id를 포함한 CSV
        MockMultipartFile csvFile = loadCsv("products_with_duplicate_external_id.csv");

        // [when & then]
        assertThatThrownBy(() ->
                productAdminService.uploadProductsByCsv(csvFile, false)
        )
                .isInstanceOf(CsvValidationException.class)
                .hasFieldOrPropertyWithValue("errorCode", CsvErrorCode.CSV_DUPLICATE_VALUE);

        // [then] 기존 데이터 외에 추가 저장 없음
        assertThat(productRepository.count()).isEqualTo(beforeCount);
    }

    @Test
    @DisplayName("CSV 내부에 external_product_id 중복이 있으면 업로드는 FAIL 한다")
    void uploadProducts_fail_whenDuplicateExternalProductIdInCsv() throws Exception {
        // [given] CSV 파일 내부에 동일한 ID가 두 번 이상 포함됨
        MockMultipartFile csvFile = loadCsv("products_duplicate_external_id.csv");

        // [when & then]
        CsvValidationException exception = assertThrows(
                CsvValidationException.class,
                () -> productAdminService.uploadProductsByCsv(csvFile, false)
        );

        // 발생한 예외의 에러 코드가 CSV_DUPLICATE_VALUE인지 검증
        assertThat(exception.getErrorCode()).isEqualTo(CsvErrorCode.CSV_DUPLICATE_VALUE);
    }

    private MockMultipartFile loadCsv(String filename) throws Exception {
        ClassPathResource resource = new ClassPathResource("csv/product/" + filename);

        return new MockMultipartFile(
                "csvFile",
                filename,
                "text/csv",
                resource.getInputStream()
        );
    }

}
