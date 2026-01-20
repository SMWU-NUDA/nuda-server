package smu.nuda.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.component.entity.Component;
import smu.nuda.domain.ingredient.entity.Ingredient;

@Entity
@Table(
        name = "product_ingredient",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_product_component_ingredient",
                        columnNames = {
                                "product_id",
                                "component_id",
                                "ingredient_id"
                        }
                )
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
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    public static ProductIngredient of(Product product, Component component, Ingredient ingredient) {
        ProductIngredient pi = new ProductIngredient();
        pi.product = product;
        pi.component = component;
        pi.ingredient = ingredient;
        return pi;
    }
}

