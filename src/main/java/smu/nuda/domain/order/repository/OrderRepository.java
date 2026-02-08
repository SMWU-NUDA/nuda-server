package smu.nuda.domain.order.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.order.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        select distinct o
        from Order o
        join fetch o.orderItems oi
        where o.memberId = :memberId
          and (:cursor is null or o.id < :cursor)
        order by o.id desc
    """)
    List<Order> findMyOrders(@Param("memberId") Long memberId, @Param("cursor") Long cursor, Pageable pageable);
}
