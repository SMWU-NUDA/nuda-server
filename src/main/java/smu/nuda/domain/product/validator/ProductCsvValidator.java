package smu.nuda.domain.product.validator;

import org.springframework.stereotype.Component;
import smu.nuda.domain.product.dto.ProductCsvRow;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;

@Component
public class ProductCsvValidator {

    public void validate(Iterable<ProductCsvRow> rows) {
        for (ProductCsvRow row : rows) {
            validateRow(row);
        }
    }

    private void validateRow(ProductCsvRow row) {
        int rowNumber = row.rowNumber();

        // 필수 칼럼 검증
        requireNotBlank(row.brandName(), rowNumber, "brand_name은 필수입니다.");
        requireNotBlank(row.categoryCode(), rowNumber, "category_code는 필수입니다.");
        requireNotBlank(row.name(), rowNumber, "name은 필수입니다.");
        requireNotBlank(row.costPrice(), rowNumber, "cost_price는 필수입니다.");
        requireNotBlank(row.discountRate(), rowNumber, "discount_rate는 필수입니다.");
        requireNotBlank(row.averageRating(), rowNumber, "average_rating은 필수입니다.");
        requireNotBlank(row.reviewCount(), rowNumber, "review_count는 필수입니다.");

        // 숫자 형식 검증
        requireInteger(row.costPrice(), rowNumber, "cost_price는 숫자여야 합니다.");
        requireInteger(row.discountRate(), rowNumber, "discount_rate는 숫자여야 합니다.");
        requireInteger(row.reviewCount(), rowNumber, "review_count는 숫자여야 합니다.");
        requireDouble(row.averageRating(), rowNumber, "average_rating은 숫자여야 합니다.");
    }

    private void requireNotBlank(String value, int rowNumber, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new CsvValidationException(CsvErrorCode.CSV_MISSING_REQUIRED_FIELD, rowNumber, message);
        }
    }

    private void requireInteger(String value, int rowNumber, String message) {
        try {
            Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new CsvValidationException(CsvErrorCode.CSV_INVALID_VALUE, rowNumber, message);
        }
    }

    private void requireDouble(String value, int rowNumber, String message) {
        try {
            Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new CsvValidationException(CsvErrorCode.CSV_INVALID_VALUE, rowNumber, message);
        }
    }

}
