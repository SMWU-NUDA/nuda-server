package smu.nuda.global.ml.dto;

public record MlRankingRequest(
        String keyword,
        int topK
) {}