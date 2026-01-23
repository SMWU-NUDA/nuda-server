package smu.nuda.domain.ingredient.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

@Entity
@Table(name = "ingredient")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "ingredient_seq",
        sequenceName = "ingredient_seq",
        allocationSize = 1
)
public class Ingredient extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ingredient_seq"
    )
    private Long id;

    @Column(length = 150, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20, nullable = false)
    private RiskLevel riskLevel;

    @Column(columnDefinition = "TEXT")
    private String content;

    public static Ingredient of(String name, RiskLevel riskLevel, String content) {
        Ingredient ingredient = new Ingredient();
        ingredient.name = name;
        ingredient.riskLevel = riskLevel;
        ingredient.content = content;
        return ingredient;
    }

    public void updateRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
}
