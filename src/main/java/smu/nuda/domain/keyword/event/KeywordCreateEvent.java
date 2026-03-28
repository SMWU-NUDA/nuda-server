package smu.nuda.domain.keyword.event;

import smu.nuda.global.ml.dto.KeywordSyncRequest;

public record KeywordCreateEvent(
        KeywordSyncRequest payload
) {
}