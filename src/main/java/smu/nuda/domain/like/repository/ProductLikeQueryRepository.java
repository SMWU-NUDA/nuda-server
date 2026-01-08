package smu.nuda.domain.like.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.like.dto.LikedProductResponse;

import java.util.List;

import static smu.nuda.domain.brand.entity.QBrand.brand;
import static smu.nuda.domain.product.entity.QProduct.product;
import static smu.nuda.domain.like.entity.QProductLike.productLike;

@Repository
@RequiredArgsConstructor
public class ProductLikeQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<LikedProductResponse> findLikedProducts(Long memberId, Long cursor, int size) {
        return queryFactory
                .select(Projections.constructor(
                        LikedProductResponse.class,
                        productLike.id,
                        product.id,
                        product.thumbnailImg,
                        brand.name,
                        product.name,
                        product.averageRating,
                        product.reviewCount
                ))
                .from(productLike)
                .join(productLike.product, product)
                .join(product.brand, brand)
                .where(
                        productLike.member.id.eq(memberId),
                        ltCursor(cursor)
                )
                .orderBy(productLike.id.desc())
                .limit(size + 1)
                .fetch();
    }

    private BooleanExpression ltCursor(Long cursor) {
        return cursor == null ? null : productLike.id.lt(cursor);
    }
}
