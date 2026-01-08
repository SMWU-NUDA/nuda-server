package smu.nuda.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.like.entity.BrandLike;

public interface BrandLikeRepository extends JpaRepository<BrandLike, Long> {
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
