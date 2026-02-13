package smu.nuda.domain.review.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.common.dto.CsvUploadResponse;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.entity.enums.CategoryCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.review.csv.ReviewCsvReader;
import smu.nuda.domain.review.dto.ReviewCsvRow;
import smu.nuda.domain.review.entity.Review;
import smu.nuda.domain.review.validator.ReviewCsvValidator;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewAdminService {

    private final ReviewCsvReader reviewCsvReader;
    private final ReviewCsvValidator reviewCsvValidator;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    private static final int BATCH_SIZE = 100;
    private static final String CSV_ADMIN_USERNAME = "csvAdmin";
    @PersistenceContext private EntityManager em;

    @Transactional
    public CsvUploadResponse uploadReviewsByCsv(MultipartFile csvFile, boolean dryRun) {
        List<ReviewCsvRow> rows = reviewCsvReader.read(csvFile);
        reviewCsvValidator.validate(rows);

        Member csvAdmin = memberRepository.findByUsername(CSV_ADMIN_USERNAME)
                .orElseThrow(() -> new IllegalStateException("csvAdmin 계정이 존재하지 않습니다."));

        persistReviewsInBatch(rows, csvAdmin, dryRun);

        int total = rows.size();
        int success = dryRun ? 0 : total;

        return new CsvUploadResponse(
                total,
                success,
                0
        );
    }

    private void persistReviewsInBatch(List<ReviewCsvRow> rows, Member member, boolean dryRun) {
        Map<String, Product> productMap = preloadProducts();

        int count = 0;
        for (ReviewCsvRow row : rows) {
            CategoryCode categoryCode = findCategoryCode(row);
            Product product = resolveProduct(productMap, row, categoryCode);

            double rating = parseRating(row);
            Review review =  new Review(member, product, rating, row.reviewContent(), null);

            if (!dryRun) {
                em.persist(review);
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

    private Map<String, Product> preloadProducts() {
        return productRepository.findAllWithCategory()
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getExternalProductId() + "_" + p.getCategory().getCode(),
                        Function.identity()
                ));
    }

    private Member findCsvAdmin() {
        return memberRepository.findByUsername(CSV_ADMIN_USERNAME)
                .orElseThrow(() ->
                        new IllegalStateException("csvAdmin 계정이 존재하지 않습니다."));
    }

    private CategoryCode findCategoryCode(ReviewCsvRow row) {
        try {
            return CategoryCode.valueOf(row.categoryCode());
        } catch (IllegalArgumentException e) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_INVALID_VALUE,
                    row.rowNumber(),
                    "존재하지 않는 category_code 입니다."
            );
        }
    }

    private Product resolveProduct(Map<String, Product> productMap, ReviewCsvRow row, CategoryCode categoryCode) {
        String key = row.externalProductId() + "_" + categoryCode.name();
        Product product = productMap.get(key);

        if (product == null) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_INVALID_REFERENCE,
                    row.rowNumber(),
                    "매핑되는 상품이 존재하지 않습니다."
            );
        }

        return product;
    }

    private double parseRating(ReviewCsvRow row) {
        try {
            double rating = Double.parseDouble(row.rating());

            if (rating < 0 || rating > 5) {
                throw new CsvValidationException(
                        CsvErrorCode.CSV_INVALID_VALUE,
                        row.rowNumber(),
                        "rating은 0~5 사이여야 합니다."
                );
            }

            return rating;

        } catch (NumberFormatException e) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_INVALID_VALUE,
                    row.rowNumber(),
                    "rating은 숫자여야 합니다."
            );
        }
    }
}
