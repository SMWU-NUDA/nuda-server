package smu.nuda.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.order.error.OrderErrorCode;
import smu.nuda.global.error.DomainException;

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
    private int unitPrice; // 단가

    @Column(nullable = false)
    private int price; // 총액

    public static OrderItem create(Order order, Long productId, int unitPrice, int quantity) {
        if (order == null) throw new DomainException(OrderErrorCode.ORDER_NOT_FOUND);
        OrderItem item = new OrderItem();
        item.order = order;
        item.productId = productId;
        item.unitPrice = unitPrice;
        item.quantity = quantity;
        item.price = unitPrice * quantity;

        // Order 연관관계 리스트에 추가
        order.addItem(item);
        return item;
    }

}
