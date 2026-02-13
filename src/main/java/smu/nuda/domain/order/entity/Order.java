package smu.nuda.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.order.entity.enums.OrderStatus;
import smu.nuda.domain.order.error.OrderErrorCode;
import smu.nuda.global.error.DomainException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_seq_generator"
    )
    @SequenceGenerator(
            name = "order_seq_generator",
            sequenceName = "seq_order_id",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "order_num", nullable = false, unique = true)
    private Long orderNum;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order create(Long memberId, Long orderNum, int totalAmount) {
        Order order = new Order();
        order.memberId = memberId;
        order.orderNum = orderNum;
        order.totalAmount = totalAmount;
        order.status = OrderStatus.PENDING;
        return order;
    }

    public boolean isPending() {
        return this.status == OrderStatus.PENDING;
    }

    public void completePayment() {
        if (!isPending()) throw new DomainException(OrderErrorCode.INVALID_ORDER_STATUS);
        this.status = OrderStatus.PAID;
    }

    public void failPayment() {
        if (isPending()) {
            this.status = OrderStatus.CANCELED;
        }
    }

    public void addItem(OrderItem item) {
        this.orderItems.add(item);
    }
}
