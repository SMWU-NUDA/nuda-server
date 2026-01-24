package smu.nuda.domain.cart.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import smu.nuda.domain.cart.entity.Cart;
import smu.nuda.domain.member.entity.Member;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("select c from Cart c where c.memberId = :memberId")
    Optional<Cart> findByMemberIdForUpdate(Long memberId);
    Optional<Cart> findByMemberId(Long memberId);
}
