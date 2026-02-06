package smu.nuda.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
