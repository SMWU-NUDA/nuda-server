package smu.nuda.domain.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.brand.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
