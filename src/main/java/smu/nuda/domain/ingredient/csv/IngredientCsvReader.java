package smu.nuda.domain.ingredient.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.ingredient.dto.IngredientCsvRow;
import smu.nuda.global.batch.csv.CsvReader;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class IngredientCsvReader implements CsvReader<IngredientCsvRow> {

    @Override
    public List<IngredientCsvRow> read(MultipartFile file) {

        List<IngredientCsvRow> rows = new ArrayList<>();

        try (
                InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withTrim()
                        .parse(reader)
        ) {
            int rowNumber = 1;

            for (CSVRecord record : parser) {
                rows.add(toRow(record, rowNumber));
                rowNumber++;
            }

            return rows;

        } catch (Exception e) {
            throw new IllegalArgumentException("CSV 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }


    private IngredientCsvRow toRow(CSVRecord record, int rowNumber) {
        return new IngredientCsvRow(
                record.get("external_product_id"),
                record.get("category_code"),
                record.get("layer_group"),
                record.get("sub_material"),
                record.isMapped("content") ? record.get("content") : null,
                rowNumber
        );
    }
}
