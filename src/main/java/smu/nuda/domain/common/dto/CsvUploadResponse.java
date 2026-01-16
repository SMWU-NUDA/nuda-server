package smu.nuda.domain.common.dto;

public record CsvUploadResponse(
        int totalCount,
        int successCount,
        int failedCount
) {}
