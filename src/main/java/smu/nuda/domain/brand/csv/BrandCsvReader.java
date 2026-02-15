package smu.nuda.domain.brand.csv;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.brand.dto.BrandCsvRow;
import smu.nuda.global.batch.csv.CsvReader;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class BrandCsvReader implements CsvReader<BrandCsvRow> {

    @Override
    public List<BrandCsvRow> read(MultipartFile file) {

        List<BrandCsvRow> rows = new ArrayList<>();

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

    private BrandCsvRow toRow(CSVRecord record, int rowNumber) {
        String logoImg = record.get("logo_img");
        if (logoImg != null && logoImg.trim().isEmpty()) logoImg = null;

        return new BrandCsvRow(
                record.get("name"),
                logoImg,
                rowNumber
        );
    }
}
