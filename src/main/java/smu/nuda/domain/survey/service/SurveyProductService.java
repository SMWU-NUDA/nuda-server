package smu.nuda.domain.survey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.domain.survey.entity.Survey;
import smu.nuda.domain.survey.entity.SurveyProduct;
import smu.nuda.domain.survey.error.SurveyErrorCode;
import smu.nuda.domain.survey.repository.SurveyProductRepository;
import smu.nuda.domain.survey.repository.SurveyRepository;
import smu.nuda.global.error.DomainException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyProductService {

    private final SurveyRepository surveyRepository;
    private final ProductRepository productRepository;
    private final SurveyProductRepository surveyProductRepository;

    @Transactional
    public void addSurveyProducts(Long surveyId, List<Long> productIds) {

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new DomainException(SurveyErrorCode.SURVEY_NOT_FOUND));

        List<Product> products = productRepository.findAllById(productIds);

        for (Product product : products) {
            if (surveyProductRepository.existsBySurveyIdAndProductId(survey.getId(), product.getId())) {
                throw new DomainException(SurveyErrorCode.SURVEY_PRODUCT_DUPLICATED);
            }
        }

        List<SurveyProduct> surveyProducts = products.stream()
                .map(product -> SurveyProduct.builder()
                        .survey(survey)
                        .product(product)
                        .build()
                )

                .toList();

        surveyProductRepository.saveAll(surveyProducts);
    }
}
