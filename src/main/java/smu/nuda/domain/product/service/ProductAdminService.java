package smu.nuda.domain.product.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.csv.ProductCsvReader;
import smu.nuda.domain.product.dto.ProductCsvRow;
import smu.nuda.domain.product.dto.ProductUploadResponse;
import smu.nuda.domain.product.entity.Brand;
import smu.nuda.domain.product.entity.Category;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.BrandRepository;
import smu.nuda.domain.product.repository.CategoryRepository;
import smu.nuda.domain.product.validator.ProductCsvValidator;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;
import smu.nuda.global.error.DomainException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAdminService {

    private final ProductCsvReader productCsvReader;
    private final ProductCsvValidator productCsvValidator;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    private static final int BATCH_SIZE = 50;
    @PersistenceContext private EntityManager em;

    @Transactional
    public ProductUploadResponse uploadProductsByCsv(MultipartFile csvFile, Member admin) {
        List<ProductCsvRow> rows = productCsvReader.read(csvFile);
        productCsvValidator.validate(rows);

        batchInsertProducts(rows); // ALL OR NOTHING

        return new ProductUploadResponse(
                rows.size(),
                rows.size(),
                0
        );
    }

    private void batchInsertProducts(List<ProductCsvRow> rows) {
        Map<String, Brand> brandMap = preloadBrands();
        Map<String, Category> categoryMap = preloadCategories();

        int count = 0;
        for (ProductCsvRow row : rows) {

            Brand brand = brandMap.get(row.brandName());
            Category category = categoryMap.get(row.categoryCode());

            if (brand == null) throw new CsvValidationException(CsvErrorCode.CSV_INVALID_REFERENCE, row.rowNumber(), "존재하지 않는 브랜드입니다.");
            if (category == null) throw new CsvValidationException(CsvErrorCode.CSV_INVALID_REFERENCE, row.rowNumber(), "존재하지 않는 카테고리입니다.");

            Product product = Product.create(
                    brand,
                    category,
                    row.name(),
                    Integer.parseInt(row.costPrice()),
                    Integer.parseInt(row.discountRate()),
                    row.content(),
                    row.thumbnailImg()
            );
            em.persist(product);
            count++;

            if (count % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }

        // 마지막 남은 것 처리
        em.flush();
        em.clear();
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

}
