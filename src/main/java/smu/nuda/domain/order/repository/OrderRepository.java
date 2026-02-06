package smu.nuda.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.order.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

}
