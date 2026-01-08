package smu.nuda.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.like.entity.ProductLike;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
