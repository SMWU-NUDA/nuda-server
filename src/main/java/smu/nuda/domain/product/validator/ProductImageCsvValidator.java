package smu.nuda.domain.product.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.product.dto.ProductImageCsvRow;
import smu.nuda.domain.product.entity.enums.ImageType;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductImageCsvValidator {

    private final ObjectMapper objectMapper;

    public void validate(Iterable<ProductImageCsvRow> rows) {
        for (ProductImageCsvRow row : rows) {
            validateRow(row);
        }
    }

    private void validateRow(ProductImageCsvRow row) {
        requireNotBlank(row.externalProductId(), row.rowNumber(), "external_product_id는 필수입니다.");
        requireNotBlank(row.type(), row.rowNumber(), "type은 필수입니다.");
        requireNotBlank(row.content(), row.rowNumber(), "content는 필수입니다.");
        requireValidImageType(row.type(), row.rowNumber());
        requireValidContentFormat(row.content(), row.rowNumber());
    }

    private void requireNotBlank(String value, int rowNumber, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new CsvValidationException(CsvErrorCode.CSV_MISSING_REQUIRED_FIELD, rowNumber, message);
        }
    }

    private static final String VALID_IMAGE_TYPES = Arrays.stream(ImageType.values())
            .map(Enum::name)
            .collect(Collectors.joining(", "));

    private void requireValidImageType(String type, int rowNumber) {
        try {
            ImageType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new CsvValidationException(CsvErrorCode.CSV_INVALID_IMAGE_TYPE, rowNumber,
                    "허용된 타입: " + VALID_IMAGE_TYPES);
        }
    }

    private void requireValidContentFormat(String content, int rowNumber) {
        try {
            String[] urls = objectMapper.readValue(content, String[].class);
            if (urls.length == 0) {
                throw new CsvValidationException(CsvErrorCode.CSV_INVALID_CONTENT_FORMAT, rowNumber);
            }
        } catch (JsonProcessingException e) {
            throw new CsvValidationException(CsvErrorCode.CSV_INVALID_CONTENT_FORMAT, rowNumber);
        }
    }
}
