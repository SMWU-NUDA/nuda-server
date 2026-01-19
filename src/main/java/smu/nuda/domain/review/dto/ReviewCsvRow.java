package smu.nuda.domain.review.dto;

public record ReviewCsvRow(
        String externalProductId,
        String categoryCode,
        String reviewContent,
        String rating,
        int rowNumber
) {}
