package smu.nuda.domain.product.dto;

public record ProductUploadResponse(
        int totalCount,
        int successCount,
        int failedCount
) {}
