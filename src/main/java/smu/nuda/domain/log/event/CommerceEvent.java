package smu.nuda.domain.log.event;

import smu.nuda.domain.log.entity.enums.CommerceType;

public record CommerceEvent(
        Long memberId,
        Long productId,
        String externalProductId,
        CommerceType commerceType,
        int quantity
) {
}
