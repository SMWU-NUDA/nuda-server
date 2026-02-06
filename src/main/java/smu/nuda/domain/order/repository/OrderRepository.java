package smu.nuda.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
