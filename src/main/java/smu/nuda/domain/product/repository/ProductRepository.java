package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.entity.enums.CategoryCode;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p.externalProductId from Product p")
    List<String> findAllExternalProductIds();
    Optional<Product> findByExternalProductIdAndCategory_Code(String externalProductId, CategoryCode categoryCode);
}
