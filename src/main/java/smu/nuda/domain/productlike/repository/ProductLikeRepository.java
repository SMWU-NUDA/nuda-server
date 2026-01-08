package smu.nuda.domain.productlike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.productlike.entity.ProductLike;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
