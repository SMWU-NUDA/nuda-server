package smu.nuda.domain.ingredient.validator;

import org.springframework.stereotype.Component;
import smu.nuda.domain.ingredient.dto.IngredientCsvRow;
import smu.nuda.global.batch.error.CsvErrorCode;
import smu.nuda.global.batch.exception.CsvValidationException;

@Component
public class IngredientCsvValidator {

    public void validate(Iterable<IngredientCsvRow> rows) {
        for (IngredientCsvRow row : rows) {
            validateRow(row);
        }
    }

    private void validateRow(IngredientCsvRow row) {
        int rowNumber = row.rowNumber();

        requireNotBlank(row.externalProductId(), rowNumber, "external_product_id는 필수입니다.");
        requireNotBlank(row.categoryCode(), rowNumber, "category_code는 필수입니다.");
        requireNotBlank(row.layerGroup(), rowNumber, "layer_group은 필수입니다.");
        requireNotBlank(row.subMaterial(), rowNumber, "sub_material은 필수입니다.");
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
