package smu.nuda.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.component.entity.Component;

@Entity
@Table(
        name = "product_component",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_product_component",
                        columnNames = {"product_id", "component_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "product_component_seq",
        sequenceName = "product_component_seq",
        allocationSize = 1
)
public class ProductComponent extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_component_seq"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    public static ProductComponent of(Product product, Component component) {
        ProductComponent pc = new ProductComponent();
        pc.product = product;
        pc.component = component;
        return pc;
    }
}
