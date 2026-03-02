package smu.nuda.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.projection.ProductRankingProjection;

import java.util.List;

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

    List<Product> findAllByIdIn(List<Long> productIds);

    @Query("""
    select p from Product p
    join fetch p.category
    """)
    List<Product> findAllWithCategory();

    @Query("""
        SELECT 
            p.id as productId,
            p.thumbnailImg as thumbnailImg,
            b.id as brandId,
            b.name as brandName,
            p.name as productName,
            p.averageRating as averageRating,
            p.reviewCount as reviewCount,
            p.likeCount as likeCount,
            p.costPrice as costPrice
        FROM Product p
        JOIN p.brand b
        WHERE p.id IN :ids
    """)
    List<ProductRankingProjection> findRankingProducts(@Param("ids") List<Long> ids);
}
