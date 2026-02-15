package smu.nuda.domain.brand.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.brand.csv.BrandCsvReader;
import smu.nuda.domain.brand.dto.BrandCsvRow;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.brand.validator.BrandCsvValidator;
import smu.nuda.domain.common.dto.CsvUploadResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandAdminService {

    private final BrandCsvReader brandCsvReader;
    private final BrandCsvValidator brandCsvValidator;

    @PersistenceContext
    private EntityManager em;

    private static final int BATCH_SIZE = 50;

    @Transactional
    public CsvUploadResponse uploadBrandsByCsv(MultipartFile file, boolean dryRun) {
        List<BrandCsvRow> rows = brandCsvReader.read(file);
        brandCsvValidator.validate(rows);

        int count = 0;
        for (BrandCsvRow row : rows) {
            Brand brand = Brand.create(row.name().trim(), row.logoImg());

            if (!dryRun) {
                em.persist(brand);
                count++;

                if (count % BATCH_SIZE == 0) {
                    em.flush();
                    em.clear();
                }
            }
        }

        if (!dryRun) {
            em.flush();
            em.clear();
        }

        return new CsvUploadResponse(
                rows.size(),
                dryRun ? 0 : rows.size(),
                0
        );
    }
}

