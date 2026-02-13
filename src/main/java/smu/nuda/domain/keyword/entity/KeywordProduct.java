package smu.nuda.domain.keyword.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.product.entity.Product;

import java.util.List;

@Entity
@Builder
@Table(
        name = "survey_product",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"survey_id", "product_id"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordProduct extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "survey_product_seq_generator"
    )
    @SequenceGenerator(
            name = "survey_product_seq_generator",
            sequenceName = "survey_product_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Keyword keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public static List<KeywordProduct> of(Keyword keyword, List<Product> products) {
        return products.stream()
                .map(product -> KeywordProduct.builder()
                        .keyword(keyword)
                        .product(product)
                        .build())
                .toList();
    }

}
