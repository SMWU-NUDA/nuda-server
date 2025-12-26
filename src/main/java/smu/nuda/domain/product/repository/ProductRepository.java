package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
