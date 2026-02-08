package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.entity.enums.CategoryCode;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p.externalProductId from Product p")
    List<String> findAllExternalProductIds();

    @Query("""
        select p
        from Product p
        join fetch p.brand
        where p.id in :ids
    """)
    List<Product> findAllWithBrandByIdIn(@Param("ids") List<Long> ids);

    Optional<Product> findByExternalProductIdAndCategory_Code(String externalProductId, CategoryCode categoryCode);
    List<Product> findAllByIdIn(List<Long> productIds);
}
