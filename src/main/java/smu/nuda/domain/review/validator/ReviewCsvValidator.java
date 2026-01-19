package smu.nuda.domain.review.validator;

import org.springframework.stereotype.Component;
import smu.nuda.domain.review.dto.ReviewCsvRow;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;

@Component
public class ReviewCsvValidator {

    public void validate(Iterable<ReviewCsvRow> rows) {
        for (ReviewCsvRow row : rows) {
            validateRow(row);
        }
    }

    private void validateRow(ReviewCsvRow row) {
        int rowNumber = row.rowNumber();

        requireNotBlank(row.externalProductId(), rowNumber, "external_product_id는 필수입니다.");
        requireNotBlank(row.categoryCode(), rowNumber, "category_code는 필수입니다.");
        requireNotBlank(row.reviewContent(), rowNumber, "review_content는 필수입니다.");
        requireNotBlank(row.rating(), rowNumber, "rating은 필수입니다.");

        requireDouble(row.rating(), rowNumber, "rating은 숫자여야 합니다.");
    }

    private void requireNotBlank(String value, int rowNumber, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_MISSING_REQUIRED_FIELD,
                    rowNumber,
                    message
            );
        }
    }

    private void requireDouble(String value, int rowNumber, String message) {
        try {
            Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_INVALID_VALUE,
                    rowNumber,
                    message
            );
        }
    }
}
