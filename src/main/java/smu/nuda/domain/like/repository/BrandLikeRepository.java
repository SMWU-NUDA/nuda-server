package smu.nuda.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.like.entity.BrandLike;
import smu.nuda.domain.member.entity.Member;

import java.util.Optional;

public interface BrandLikeRepository extends JpaRepository<BrandLike, Long> {
    boolean existsByMember_IdAndBrand_Id(Long memberId, Long brandId);
    Optional<BrandLike> findByMemberAndBrand(Member member, Brand brand);
}
