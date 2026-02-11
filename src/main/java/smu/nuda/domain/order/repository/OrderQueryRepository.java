package smu.nuda.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.order.entity.Order;

import java.util.List;

import static smu.nuda.domain.order.entity.QOrder.order;
import static smu.nuda.domain.order.entity.QOrderItem.orderItem;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<Order> findMyOrders(Long memberId, Long cursor, int size) {
        List<Long> ids = queryFactory
                .select(order.id)
                .from(order)
                .where(
                        order.memberId.eq(memberId),
                        cursor != null ? order.id.lt(cursor) : null
                )
                .orderBy(order.id.desc())
                .limit(size)
                .fetch();

        if (ids.isEmpty()) return List.of();
        return queryFactory
                .selectFrom(order)
                .join(order.orderItems, orderItem).fetchJoin()
                .where(order.id.in(ids))
                .orderBy(order.id.desc())
                .fetch();
    }

}
