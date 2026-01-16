package smu.nuda.domain.signupdraft.policy;

import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.signupdraft.error.SignupDraftErrorCode;
import smu.nuda.global.error.DomainException;

import java.util.HashSet;
import java.util.List;

public class SurveyProductPolicy {
    public static void validate(List<Long> requestedIds, List<Product> foundProducts) {
        validateNoDuplicate(requestedIds);
        validateAllExist(requestedIds, foundProducts);
    }

    private static void validateNoDuplicate(List<Long> ids) {
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new DomainException(SignupDraftErrorCode.DUPLICATED_SURVEY_PRODUCT_SELECTION);
        }
    }

    private static void validateAllExist(List<Long> requestedIds, List<Product> foundProducts) {
        if (requestedIds.size() != foundProducts.size()) {
            throw new DomainException(SignupDraftErrorCode.SURVEY_PRODUCT_NOT_FOUND);
        }
    }
}
