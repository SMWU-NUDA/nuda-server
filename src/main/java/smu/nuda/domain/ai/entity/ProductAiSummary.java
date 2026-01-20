package smu.nuda.domain.ai.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.product.entity.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product_ai_summary")
public class ProductAiSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_ai_summary_seq")
    @SequenceGenerator(
            name = "product_ai_summary_seq",
            sequenceName = "product_ai_summary_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    private double satisfactionRate;

    @Column(columnDefinition = "jsonb")
    private String positiveKeywords;

    @Column(columnDefinition = "jsonb")
    private String negativeKeywords;

    @Column(columnDefinition = "TEXT")
    private String trendSummary;
}
