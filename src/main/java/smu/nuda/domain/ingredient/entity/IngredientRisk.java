package smu.nuda.domain.ingredient.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;
import smu.nuda.domain.ingredient.entity.enums.RiskSource;

@Entity
@Table(name = "ingredient_risk")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "ingredient_risk_seq",
        sequenceName = "ingredient_risk_seq",
        allocationSize = 1
)
public class IngredientRisk extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ingredient_risk_seq"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private RiskSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20, nullable = false)
    private RiskLevel riskLevel;

    @Column(length = 255)
    private String toxicity;

    @Lob
    private String referenceUrl;

    public static IngredientRisk of(Ingredient ingredient, RiskSource source, RiskLevel riskLevel, String toxicity, String referenceUrl) {
        IngredientRisk risk = new IngredientRisk();
        risk.ingredient = ingredient;
        risk.source = source;
        risk.riskLevel = riskLevel;
        risk.toxicity = toxicity;
        risk.referenceUrl = referenceUrl;
        return risk;
    }
}
