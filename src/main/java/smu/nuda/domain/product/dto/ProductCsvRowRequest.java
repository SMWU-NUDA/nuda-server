package smu.nuda.domain.product.dto;

public record ProductCsvRowRequest(

        String brandName,
        String categoryCode,
        String name,
        String costPrice,
        String discountRate,

        String content,
        String thumbnailImg,

        int rowNumber
) {}
