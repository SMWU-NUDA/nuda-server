package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.product.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
