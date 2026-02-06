package smu.nuda.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.order.entity.Order;
import smu.nuda.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrder(Order order);
}
