package smu.nuda.domain.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static smu.nuda.domain.product.entity.QProductImage.productImage;

@Repository
@RequiredArgsConstructor
public class ProductImageQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<String> findImageUrlsByProductId(Long productId) {
        return queryFactory
                .select(productImage.imageUrl)
                .from(productImage)
                .where(productImage.product.id.eq(productId))
                .orderBy(productImage.sequence.asc())
                .fetch();
    }
}
