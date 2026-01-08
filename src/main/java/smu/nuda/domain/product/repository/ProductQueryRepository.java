package smu.nuda.domain.product.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.product.dto.ProductDetailCache;
import static smu.nuda.domain.product.entity.QProduct.product;
import static smu.nuda.domain.brand.entity.QBrand.brand;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ProductDetailCache findProductBase(Long productId) {

        return queryFactory
                .select(Projections.constructor(
                        ProductDetailCache.class,
                        product.id,
                        Expressions.constant(List.of()), // imageUrls는 나중에
                        brand.name,
                        product.name,
                        product.averageRating,
                        product.reviewCount,
                        product.costPrice,
                        product.content
                ))
                .from(product)
                .join(brand).on(product.brand.id.eq(brand.id))
                .where(product.id.eq(productId))
                .fetchOne();
    }
}
