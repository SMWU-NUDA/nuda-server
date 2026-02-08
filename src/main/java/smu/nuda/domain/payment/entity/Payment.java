package smu.nuda.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.order.entity.Order;
import smu.nuda.domain.payment.entity.enums.PaymentStatus;
import smu.nuda.domain.payment.error.PaymentErrorCode;
import smu.nuda.global.error.DomainException;

import java.time.Clock;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payment",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_payment_key", columnNames = "payment_key"),
                @UniqueConstraint(name = "uk_payment_order_id", columnNames = "order_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "payment_seq"
    )
    @SequenceGenerator(
            name = "payment_seq",
            sequenceName = "seq_payment_id",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "payment_key", nullable = false, length = 100)
    private String paymentKey;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public static Payment request(Order order, String paymentKey, int amount, Clock clock) {
        Payment payment = new Payment();
        payment.order = order;
        payment.paymentKey = paymentKey;
        payment.amount = amount;
        payment.status = PaymentStatus.REQUESTED;
        payment.requestedAt = LocalDateTime.now(clock);
        return payment;
    }

    public void approve() {
        validateApprovable();
        this.status = PaymentStatus.SUCCESS;
        this.approvedAt = LocalDateTime.now();
    }

    public void fail() {
        if (this.status != PaymentStatus.REQUESTED) {
            return;
        }
        this.status = PaymentStatus.FAILED;
    }

    private void validateApprovable() {
        if (this.status != PaymentStatus.REQUESTED) {
            throw new DomainException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }
}
