package smu.nuda.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.ingredient.entity.Ingredient;

@Entity
@Table(
        name = "product_ingredient",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_product_ingredient",
                        columnNames = {"product_id", "ingredient_id"}
                )
        },
        indexes = {
                @Index(name = "idx_product_ingredient_product", columnList = "product_id"),
                @Index(name = "idx_product_ingredient_ingredient", columnList = "ingredient_id"),
                @Index(name = "ix_product_ingredient_external_product_id", columnList = "external_product_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "product_ingredient_seq",
        sequenceName = "product_ingredient_seq",
        allocationSize = 1
)
public class ProductIngredient extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_ingredient_seq"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_ingredient_product")
    )
    private Product product;

    @Column(name = "external_product_id", length = 30)
    private String externalProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ingredient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_ingredient_ingredient")
    )
    private Ingredient ingredient;

}
