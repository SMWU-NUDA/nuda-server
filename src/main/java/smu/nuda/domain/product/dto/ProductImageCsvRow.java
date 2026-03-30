package smu.nuda.domain.product.dto;

public record ProductImageCsvRow(
        String externalProductId,
        String type,
        String content,
        int rowNumber
) {}
