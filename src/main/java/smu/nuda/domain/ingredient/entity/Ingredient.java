package smu.nuda.domain.ingredient.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.ingredient.entity.enums.LayerType;
import smu.nuda.domain.ingredient.entity.enums.RiskLevel;

@Entity
@Table(name = "ingredient",
        indexes = {
                @Index(name = "idx_ingredient_name", columnList = "name")
        }
)
@SequenceGenerator(
        name = "ingredient_seq",
        sequenceName = "ingredient_seq",
        allocationSize = 1
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LayerType layerType;

    @Column(columnDefinition = "TEXT")
    private String content;

}
