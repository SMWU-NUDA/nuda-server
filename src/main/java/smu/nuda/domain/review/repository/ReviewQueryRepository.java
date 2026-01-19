package smu.nuda.domain.review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.review.dto.MyReviewResponse;

import java.util.List;

import static smu.nuda.domain.review.entity.QReview.review;
import static smu.nuda.domain.product.entity.QProduct.product;
import static smu.nuda.domain.brand.entity.QBrand.brand;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<MyReviewResponse> findMyReviews(Long memberId, Long cursor, int size) {
        return queryFactory
                .select(Projections.constructor(
                        MyReviewResponse.class,
                        product.id,
                        product.thumbnailImg,
                        product.name,
                        brand.name,
                        review.id,
                        review.rating,
                        review.content,
                        review.createdAt.stringValue()
                ))
                .from(review)
                .join(review.product, product)
                .join(product.brand, brand)
                .where(
                        review.member.id.eq(memberId),
                        cursor != null ? review.id.lt(cursor) : null
                )
                .orderBy(review.id.desc())
                .limit(size + 1)
                .fetch();
    }
}
