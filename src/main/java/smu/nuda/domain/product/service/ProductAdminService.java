package smu.nuda.domain.product.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.product.csv.ProductCsvReader;
import smu.nuda.domain.product.dto.ProductCsvRow;
import smu.nuda.domain.common.dto.CsvUploadResponse;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.product.entity.Category;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.brand.repository.BrandRepository;
import smu.nuda.domain.product.repository.CategoryRepository;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.product.validator.ProductCsvValidator;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAdminService {

    private final ProductCsvReader productCsvReader;
    private final ProductCsvValidator productCsvValidator;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private static final int BATCH_SIZE = 50;
    @PersistenceContext private EntityManager em;

    @Transactional
    public CsvUploadResponse uploadProductsByCsv(MultipartFile csvFile, boolean dryRun) {
        List<ProductCsvRow> rows = productCsvReader.read(csvFile);
        productCsvValidator.validate(rows);

        persistProductsInBatch(rows, dryRun); // ALL OR NOTHING

        int total = rows.size();
        int success = dryRun ? 0 : total;

        return new CsvUploadResponse(
                total,
                success,
                0
        );
    }

    private void persistProductsInBatch(List<ProductCsvRow> rows, boolean dryRun) {
        Map<String, Brand> brandMap = preloadBrands();
        Map<String, Category> categoryMap = preloadCategories();

        Set<String> existingExternalIds = new HashSet<>(preloadExistingExternalIds());
        Set<String> seenExternalIdsInCsv = new HashSet<>();

        int count = 0;
        for (ProductCsvRow row : rows) {
            String externalId = row.externalProductId();
            Brand brand = brandMap.get(row.brandName());
            Category category = categoryMap.get(row.categoryCode());

            if (!seenExternalIdsInCsv.add(externalId))
                throw new CsvValidationException(CsvErrorCode.CSV_DUPLICATE_VALUE, row.rowNumber(), "CSV 내 external_product_id 중복입니다.");
            if (existingExternalIds.contains(externalId))
                throw new CsvValidationException(CsvErrorCode.CSV_DUPLICATE_VALUE, row.rowNumber(), "이미 존재하는 external_product_id 입니다.");
            if (brand == null) throw new CsvValidationException(CsvErrorCode.CSV_INVALID_REFERENCE, row.rowNumber(), "존재하지 않는 브랜드입니다.");
            if (category == null) throw new CsvValidationException(CsvErrorCode.CSV_INVALID_REFERENCE, row.rowNumber(), "존재하지 않는 카테고리입니다.");

            Product product = toProduct(row, brand, category);

            if (!dryRun) {
                em.persist(product);
                count++;

                if (count % BATCH_SIZE == 0) {
                    em.flush();
                    em.clear();
                }
            }
        }

        if (!dryRun) {
            em.flush();
            em.clear();
        }
    }

    private Map<String, Brand> preloadBrands() {
        return brandRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Brand::getName,
                        Function.identity()
                ));
    }

    private Map<String, Category> preloadCategories() {
        return categoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        category -> category.getCode().name(),
                        Function.identity()
                ));
    }

    private Set<String> preloadExistingExternalIds() {
        return new HashSet<>(productRepository.findAllExternalProductIds());
    }

    private Product toProduct(ProductCsvRow row, Brand brand, Category category) {
        int costPrice = parseInt(row.costPrice(), row);
        int discountRate = parseInt(row.discountRate(), row);
        double averageRating = parseDouble(row.averageRating(), row);
        int reviewCount = parseInt(row.reviewCount(), row);

        return Product.create(
                row.externalProductId(),
                brand,
                category,
                row.name(),
                costPrice,
                discountRate,
                row.content(),
                row.thumbnailImg(),
                averageRating,
                reviewCount
        );
    }

    private int parseInt(String value, ProductCsvRow row) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_INVALID_VALUE,
                    row.rowNumber(),
                    "숫자 형식이 올바르지 않습니다. int 형식을 확인해주세요."
            );
        }
    }

    private double parseDouble(String value, ProductCsvRow row) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_INVALID_VALUE,
                    row.rowNumber(),
                    "숫자 형식이 올바르지 않습니다. double 형식을 확인해주세요."
            );
        }
    }


}
