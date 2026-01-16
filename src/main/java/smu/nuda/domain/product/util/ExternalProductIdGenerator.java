package smu.nuda.domain.product.util;

import java.util.UUID;

public final class ExternalProductIdGenerator {

    private static final String INTERNAL_PREFIX = "INTERNAL";

    private ExternalProductIdGenerator() {}

    public static String generateInternalId() {
        return INTERNAL_PREFIX + "-" + UUID.randomUUID();
    }
}
