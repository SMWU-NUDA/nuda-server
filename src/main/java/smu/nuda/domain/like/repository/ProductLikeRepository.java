package smu.nuda.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.like.entity.ProductLike;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.entity.Product;

import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByMember_IdAndProduct_Id(Long memberId, Long productId);
    Optional<ProductLike> findByMemberAndProduct(Member member, Product product);
}
