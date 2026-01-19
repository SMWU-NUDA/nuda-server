package smu.nuda.domain.review.csv;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.review.dto.ReviewCsvRow;
import smu.nuda.global.batch.csv.CsvReader;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewCsvReader implements CsvReader<ReviewCsvRow> {

    @Override
    public List<ReviewCsvRow> read(MultipartFile file) {

        List<ReviewCsvRow> rows = new ArrayList<>();

        try (
                InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
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

    private ReviewCsvRow toRow(CSVRecord record, int rowNumber) {
        return new ReviewCsvRow(
                record.get("external_product_id"),
                record.get("category_code"),
                record.get("review_content"),
                record.get("rating"),
                rowNumber
        );
    }
}
