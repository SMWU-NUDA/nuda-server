package smu.nuda.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_item_seq"
    )
    @SequenceGenerator(
            name = "order_item_seq",
            sequenceName = "seq_order_item_id",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(nullable = false)
    private int price;

    public static OrderItem create(Long productId, int quantity, int unitPrice) {
        OrderItem item = new OrderItem();
        item.productId = productId;
        item.quantity = quantity;
        item.unitPrice = unitPrice;
        item.price = quantity * unitPrice;
        return item;
    }

}
