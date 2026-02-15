package smu.nuda.domain.brand.validator;

import org.springframework.stereotype.Component;
import smu.nuda.domain.brand.dto.BrandCsvRow;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;

import java.util.HashSet;
import java.util.Set;

@Component
public class BrandCsvValidator {

    public void validate(Iterable<BrandCsvRow> rows) {
        Set<String> seenNames = new HashSet<>();

        for (BrandCsvRow row : rows) {
            validateRow(row);
            validateDuplicateNames(row, seenNames);
        }
    }

    private void validateRow(BrandCsvRow row) {
        requireNotBlank(row.name(), row.rowNumber(), "name은 필수입니다.");
    }

    private void validateDuplicateNames(BrandCsvRow row, Set<String> seenNames) {
        String normalized = row.name().trim().toLowerCase();

        if (!seenNames.add(normalized)) {
            throw new CsvValidationException(
                    CsvErrorCode.CSV_DUPLICATE_VALUE,
                    row.rowNumber(),
                    "CSV 내부에 중복된 브랜드명이 존재합니다."
            );
        }
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
}
