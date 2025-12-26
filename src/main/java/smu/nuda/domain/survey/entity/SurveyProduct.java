package smu.nuda.domain.survey.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.product.entity.Product;

@Entity
@Table(
        name = "survey_product",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"survey_id", "product_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}

