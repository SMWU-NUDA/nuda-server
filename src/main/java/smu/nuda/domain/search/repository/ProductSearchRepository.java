package smu.nuda.domain.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import smu.nuda.domain.search.document.ProductDocument;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
}
