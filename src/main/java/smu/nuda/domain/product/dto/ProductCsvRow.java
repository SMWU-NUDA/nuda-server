package smu.nuda.domain.product.dto;

public record ProductCsvRow(

        String brandName,
        String categoryCode,
        String name,
        String costPrice,
        String discountRate,

        String content,
        String thumbnailImg,

        int rowNumber
) {}
