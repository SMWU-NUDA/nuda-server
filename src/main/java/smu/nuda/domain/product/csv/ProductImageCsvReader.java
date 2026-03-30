package smu.nuda.domain.product.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.product.dto.ProductImageCsvRow;
import smu.nuda.global.batch.csv.CsvReader;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductImageCsvReader implements CsvReader<ProductImageCsvRow> {

    @Override
    public List<ProductImageCsvRow> read(MultipartFile file) {

        List<ProductImageCsvRow> rows = new ArrayList<>();

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

    private ProductImageCsvRow toRow(CSVRecord record, int rowNumber) {
        return new ProductImageCsvRow(
                record.isMapped("external_product_id") ? record.get("external_product_id") : null,
                record.isMapped("type") ? record.get("type") : null,
                record.isMapped("content") ? record.get("content") : null,
                rowNumber
        );
    }
}
