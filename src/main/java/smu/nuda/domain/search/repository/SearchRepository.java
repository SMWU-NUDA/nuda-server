package smu.nuda.domain.search.repository;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.search.document.ProductDocument;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SearchRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchHits<ProductDocument> searchProducts(String keyword, int page, int size) {
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
        return elasticsearchOperations.search(query, ProductDocument.class);
    }

    public List<String> suggestProductNames(String keyword, int limit) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .matchPhrasePrefix(m -> m
                                .field("productName")
                                .query(keyword)
                                .maxExpansions(10)
                        )
                )
                .withSourceFilter(new FetchSourceFilter(new String[]{"productName"}, null))
                .withPageable(PageRequest.of(0, limit))
                .build();

        SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);
        return hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getProductName())
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void indexAllProducts(List<ProductDocument> docs) {
        elasticsearchOperations.save(docs);
    }
}
