package smu.nuda.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.csv.ProductCsvReader;
import smu.nuda.domain.product.dto.ProductCsvRowRequest;
import smu.nuda.domain.product.dto.ProductUploadResponse;
import smu.nuda.domain.product.validator.ProductCsvValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAdminService {

    private final ProductCsvReader productCsvReader;
    private final ProductCsvValidator productCsvValidator;

    @Transactional
    public ProductUploadResponse uploadProductsByCsv(MultipartFile csvFile, Member admin) {
        List<ProductCsvRowRequest> rows = productCsvReader.read(csvFile);
        productCsvValidator.validate(rows);

        // Todo. batch insert

        return new ProductUploadResponse(
                rows.size(),
                rows.size(),
                0
        );
    }
}
