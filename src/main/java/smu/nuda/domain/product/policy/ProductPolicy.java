package smu.nuda.domain.product.policy;

import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.global.error.DomainException;

public class ProductPolicy {
    public static void validateCreate(int costPrice, int discountRate, double averageRating, int reviewCount) {
        if (costPrice < 0) {
            throw new DomainException(ProductErrorCode.INVALID_COST_PRICE);
        }
        if (discountRate < 0 || discountRate > 100) {
            throw new DomainException(ProductErrorCode.INVALID_DISCOUNT_RATE);
        }
        if (averageRating < 0 || averageRating > 5) {
            throw new DomainException(ProductErrorCode.INVALID_AVERAGE_RATING);
        }
        if (reviewCount < 0) {
            throw new DomainException(ProductErrorCode.INVALID_REVIEW_COUNT);
        }
    }
}
