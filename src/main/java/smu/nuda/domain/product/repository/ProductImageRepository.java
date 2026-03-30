package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.entity.ProductImage;
import smu.nuda.domain.product.entity.enums.ImageType;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ProductImage pi where pi.product = :product and pi.type = :type")
    void deleteAllByProductAndType(@Param("product") Product product, @Param("type") ImageType type);
}
