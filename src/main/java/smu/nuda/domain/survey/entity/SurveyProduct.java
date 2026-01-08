package smu.nuda.domain.survey.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.product.entity.Product;

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
public class SurveyProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "survey_product_seq")
    @SequenceGenerator(
            name = "survey_product_seq",
            sequenceName = "survey_product_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}

