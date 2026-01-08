package smu.nuda.domain.like.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.like.dto.LikedBrandResponse;

import java.util.List;

import static smu.nuda.domain.brand.entity.QBrand.brand;
import static smu.nuda.domain.like.entity.QBrandLike.brandLike;
import static smu.nuda.domain.like.entity.QProductLike.productLike;

@Repository
@RequiredArgsConstructor
public class BrandLikeQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<LikedBrandResponse> findLikedBrands(Long memberId, Long cursor, int size) {
        return queryFactory
                .select(Projections.constructor(
                        LikedBrandResponse.class,
                        brandLike.id,
                        brand.id,
                        brand.logoImg,
                        brand.name
                ))
                .from(brandLike)
                .join(brandLike.brand, brand)
                .where(
                        brandLike.member.id.eq(memberId),
                        ltCursor(cursor)
                )
                .orderBy(brandLike.id.desc())
                .limit(size + 1)
                .fetch();
    }

    private BooleanExpression ltCursor(Long cursor) {
        return cursor == null ? null : productLike.id.lt(cursor);
    }
}
