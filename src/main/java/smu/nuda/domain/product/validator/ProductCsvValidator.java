package smu.nuda.domain.product.validator;

import org.springframework.stereotype.Component;
import smu.nuda.domain.product.dto.ProductCsvRowRequest;
import smu.nuda.global.batch.exception.CsvValidationException;

@Component
public class ProductCsvValidator {

    public void validate(Iterable<ProductCsvRowRequest> rows) {
        for (ProductCsvRowRequest row : rows) {
            validateRow(row);
        }
    }

    private void validateRow(ProductCsvRowRequest row) {
        int rowNumber = row.rowNumber();

        // 필수 칼럼 검증
        requireNotBlank(row.brandName(), rowNumber, "brand_name은 필수입니다.");
        requireNotBlank(row.categoryCode(), rowNumber, "category_code는 필수입니다.");
        requireNotBlank(row.name(), rowNumber, "name은 필수입니다.");
        requireNotBlank(row.costPrice(), rowNumber, "cost_price는 필수입니다.");
        requireNotBlank(row.discountRate(), rowNumber, "discount_rate는 필수입니다.");

        // 숫자 형식 검증
        requireInteger(row.costPrice(), rowNumber, "cost_price는 숫자여야 합니다.");
        requireInteger(row.discountRate(), rowNumber, "discount_rate는 숫자여야 합니다.");
    }

    private void requireNotBlank(String value, int rowNumber, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new CsvValidationException(rowNumber, message);
        }
    }

    private void requireInteger(String value, int rowNumber, String message) {
        try {
            Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new CsvValidationException(rowNumber, message);
        }
    }
}
