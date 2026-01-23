package smu.nuda.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smu.nuda.domain.cart.entity.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("""
        select ci
        from CartItem ci
        join ci.cart c
        where c.memberId = :memberId
          and ci.productId = :productId
    """)
    Optional<CartItem> findByMemberIdAndProductId(Long memberId, Long productId);
}
