package smu.nuda.domain.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.product.csv.ProductCsvReader;
import smu.nuda.domain.product.csv.ProductImageCsvReader;
import smu.nuda.domain.product.dto.ProductCsvRow;
import smu.nuda.domain.common.dto.CsvUploadResponse;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.product.dto.ProductImageCsvRow;
import smu.nuda.domain.product.entity.Category;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.brand.repository.BrandRepository;
import smu.nuda.domain.product.entity.ProductImage;
import smu.nuda.domain.product.entity.enums.ImageType;
import smu.nuda.domain.product.repository.CategoryRepository;
import smu.nuda.domain.product.repository.ProductImageRepository;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.product.validator.ProductCsvValidator;
import smu.nuda.domain.product.validator.ProductImageCsvValidator;
import smu.nuda.domain.search.document.ProductDocument;
import smu.nuda.domain.search.event.ProductIndexingEvent;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAdminService {

    private final ProductImageCsvReader productImageCsvReader;
    private final ProductImageCsvValidator productImageCsvValidator;
    private final ProductImageRepository productImageRepository;
    private final ObjectMapper objectMapper;
    private final ProductCsvReader productCsvReader;
    private final ProductCsvValidator productCsvValidator;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

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

        List<ProductDocument> docs = new ArrayList<>();
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

                if (row.thumbnailImg() != null && !row.thumbnailImg().isBlank()) {
                    ProductImage thumbnail = ProductImage.create(
                            product,
                            row.thumbnailImg(),
                            0,
                            ImageType.MAIN
                    );
                    em.persist(thumbnail);
                }

                docs.add(toDocument(product, row, brand));
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
            eventPublisher.publishEvent(new ProductIndexingEvent(docs));
        }
    }

    private ProductDocument toDocument(Product product, ProductCsvRow row, Brand brand) {
        return ProductDocument.builder()
                .id(String.valueOf(product.getId()))
                .productId(product.getId())
                .productName(product.getName())
                .ingredientNames(List.of())
                .brandId(brand.getId())
                .brandName(brand.getName())
                .thumbnailImg(row.thumbnailImg())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .likeCount(product.getLikeCount())
                .costPrice(product.getCostPrice())
                .build();
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

    @Transactional
    public CsvUploadResponse uploadContentImagesByCsv(MultipartFile csvFile, boolean dryRun) {
        List<ProductImageCsvRow> rows = productImageCsvReader.read(csvFile);
        productImageCsvValidator.validate(rows);

        persistProductImagesInBatch(rows, dryRun); // ALL OR NOTHING

        int total = rows.size();
        int success = dryRun ? 0 : total;
        return new CsvUploadResponse(total, success, 0);
    }

    private void persistProductImagesInBatch(List<ProductImageCsvRow> rows, boolean dryRun) {
        List<String> externalIds = rows.stream().map(ProductImageCsvRow::externalProductId).toList();
        Map<String, Product> productMap = productRepository.findAllByExternalProductIdIn(externalIds)
                .stream()
                .collect(Collectors.toMap(
                        Product::getExternalProductId,
                        Function.identity(),
                        (a, b) -> a
                ));

        for (ProductImageCsvRow row : rows) {
            if (!productMap.containsKey(row.externalProductId())) {
                throw new CsvValidationException(CsvErrorCode.CSV_INVALID_REFERENCE, row.rowNumber(), "존재하지 않는 상품입니다.");
            }
        }

        if (dryRun) return;

        int imageCount = 0;
        for (ProductImageCsvRow row : rows) {
            ImageType imageType = ImageType.valueOf(row.type());
            Product product = productMap.get(row.externalProductId());

            // deleteAllByProductAndType는 clearAutomatically=true → 삭제 후 PC 초기화 → product가 detached 상태가 됨. merge로 재첨부 후 이미지 저장
            productImageRepository.deleteAllByProductAndType(product, imageType);
            product = em.merge(product);

            String[] urls;
            try {
                urls = objectMapper.readValue(row.content(), String[].class);
            } catch (JsonProcessingException e) {
                throw new CsvValidationException(CsvErrorCode.CSV_INVALID_CONTENT_FORMAT, row.rowNumber());
            }

            for (int i = 0; i < urls.length; i++) {
                em.persist(ProductImage.create(product, urls[i], i, imageType));
                imageCount++;
                if (imageCount % BATCH_SIZE == 0) {
                    em.flush();
                    em.clear();
                    product = em.merge(product); // 남은 URL 처리를 위해 재첨부
                }
            }
        }

        em.flush();
        em.clear();
    }

}
