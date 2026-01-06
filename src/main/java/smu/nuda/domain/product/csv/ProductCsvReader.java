package smu.nuda.domain.product.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.product.dto.ProductCsvRowRequest;
import smu.nuda.global.batch.csv.CsvReader;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductCsvReader implements CsvReader<ProductCsvRowRequest> {

    @Override
    public List<ProductCsvRowRequest> read(MultipartFile file) {

        List<ProductCsvRowRequest> rows = new ArrayList<>();

        try (
                InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withTrim()
                        .parse(reader)
        ) {
            int rowNumber = 1; // header 제외

            for (CSVRecord record : parser) {
                rows.add(toRow(record, rowNumber));
                rowNumber++;
            }

            return rows;

        } catch (Exception e) {
            throw new IllegalArgumentException("CSV 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    private ProductCsvRowRequest toRow(CSVRecord record, int rowNumber) {
        return new ProductCsvRowRequest(
                record.get("brand_name"),
                record.get("category_code"),
                record.get("name"),
                record.get("cost_price"),
                record.get("discount_rate"),
                record.isMapped("content") ? record.get("content") : null,
                record.isMapped("thumbnail_img") ? record.get("thumbnail_img") : null,
                rowNumber
        );
    }
}
