package smu.nuda.domain.product.dto;

public record ProductCsvRow(
        String externalProductId,

        String brandName,
        String categoryCode,
        String name,
        String costPrice,
        String discountRate,

        String content,
        String thumbnailImg,

        String averageRating,
        String reviewCount,

        int rowNumber
) {}
