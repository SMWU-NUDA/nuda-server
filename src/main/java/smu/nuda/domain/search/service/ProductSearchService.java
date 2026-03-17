package smu.nuda.domain.search.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.product.dto.ProductItem;
import smu.nuda.domain.search.document.ProductDocument;
import smu.nuda.domain.search.error.SearchErrorCode;
import smu.nuda.domain.search.repository.ProductSearchRepository;
import smu.nuda.global.error.DomainException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public CursorPageResponse<ProductItem> search(String keyword, Long cursor, int size) {
        int page = cursor == null ? 0 : cursor.intValue();

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("productName^3", "ingredientNames")
                                .query(keyword)
                                .type(TextQueryType.MostFields)
                                .operator(Operator.Or)
                        )
                )
                .withSort(SortOptions.of(s -> s.score(sc -> sc.order(SortOrder.Desc))))
                .withSort(SortOptions.of(s -> s.field(f -> f.field("productId").order(SortOrder.Desc))))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);
        int totalPages = (int) Math.ceil((double) hits.getTotalHits() / size);

        List<ProductItem> items = hits.getSearchHits().stream()
                .map(hit -> toProductItem(hit.getContent()))
                .collect(Collectors.toList());

        return CursorPageResponse.of(items, page + 1, totalPages);
    }

    public void index(ProductDocument doc) {
        productSearchRepository.save(doc);
    }

    public void indexAll(List<ProductDocument> docs) {
        productSearchRepository.saveAll(docs);
    }

    private ProductItem toProductItem(ProductDocument doc) {
        ProductItem item = new ProductItem(
                doc.getProductId(),
                doc.getThumbnailImg(),
                doc.getBrandId(),
                doc.getBrandName(),
                doc.getProductName(),
                doc.getAverageRating(),
                doc.getReviewCount(),
                doc.getLikeCount(),
                doc.getCostPrice()
        );
        item.setIngredientLabels(doc.getIngredientNames());
        return item;
    }
}
