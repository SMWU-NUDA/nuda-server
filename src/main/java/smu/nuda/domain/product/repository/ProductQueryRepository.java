package smu.nuda.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.nuda.domain.common.dto.Cursor;
import smu.nuda.domain.common.dto.CursorResponse;
import smu.nuda.domain.ingredient.entity.Ingredient;
import smu.nuda.domain.product.dto.ProductDetailCache;
import smu.nuda.domain.product.dto.ProductItem;
import smu.nuda.domain.product.dto.enums.ProductSortType;

import static smu.nuda.domain.ingredient.entity.QIngredient.ingredient;
import static smu.nuda.domain.product.entity.QProduct.product;
import static smu.nuda.domain.brand.entity.QBrand.brand;
import static smu.nuda.domain.product.entity.QProductIngredient.productIngredient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ProductDetailCache findProductBase(Long productId) {

        return queryFactory
                .select(Projections.constructor(
                        ProductDetailCache.class,
                        product.id,
                        Expressions.constant(List.of()), // mainImageUrls 나중에
                        brand.id,
                        brand.name,
                        product.name,
                        Expressions.constant(List.of()), // ingredientLabels 나중에
                        product.averageRating,
                        product.reviewCount,
                        product.costPrice,
                        Expressions.constant(List.of()), // detailImageUrls 나중에
                        product.content
                ))
                .from(product)
                .join(product.brand, brand)
                .where(product.id.eq(productId))
                .fetchOne();
    }

    public CursorResponse<ProductItem, Number> findProductCursorPage(ProductSortType sortType, Cursor<Number> cursor, int size) {
        BooleanBuilder builder = new BooleanBuilder();

        applyCursorCondition(builder, sortType, cursor);

        // 정렬 기준
        OrderSpecifier<?> primaryOrder = getPrimaryOrder(sortType);

        List<ProductItem> results = queryFactory
                .select(Projections.constructor(
                        ProductItem.class,
                        product.id,
                        product.thumbnailImg,
                        brand.id,
                        brand.name,
                        product.name,
                        product.averageRating,
                        product.reviewCount,
                        product.likeCount,
                        product.costPrice
                ))
                .from(product)
                .join(product.brand, brand)
                .where(builder)
                .orderBy(primaryOrder, product.id.desc())
                .limit(size + 1)
                .fetch();

        return CursorResponse.of(
                results,
                size,
                item -> extractCursor(sortType, item)
        );
    }

    private OrderSpecifier<?> getPrimaryOrder(ProductSortType sortType) {
        return switch (sortType) {
            case REVIEW_COUNT_DESC -> product.reviewCount.desc();
            case RATING_DESC -> product.averageRating.desc();
            case RATING_ASC -> product.averageRating.asc();
            case LIKE_COUNT_DESC -> product.likeCount.desc();
            case DEFAULT -> product.id.desc();
        };
    }

    private void applyCursorCondition(BooleanBuilder builder, ProductSortType sortType, Cursor<Number> cursor) {
        if (cursor == null) return;

        Number sortValue = cursor.getSortValue();
        Long id = cursor.getId();
        switch (sortType) {

            // 리뷰 많은 순
            case REVIEW_COUNT_DESC -> builder.and(
                    product.reviewCount.lt(sortValue.intValue())
                            .or(product.reviewCount.eq(sortValue.intValue())
                                    .and(product.id.lt(id)))
            );

            // 별점 높은 순
            case RATING_DESC -> builder.and(
                    product.averageRating.lt(sortValue.doubleValue())
                            .or(product.averageRating.eq(sortValue.doubleValue())
                                    .and(product.id.lt(id)))
            );

            // 별점 낮은 순
            case RATING_ASC -> builder.and(
                    product.averageRating.gt(sortValue.doubleValue())
                            .or(product.averageRating.eq(sortValue.doubleValue())
                                    .and(product.id.lt(id)))
            );

            // 찜 많은 순
            case LIKE_COUNT_DESC -> builder.and(
                    product.likeCount.lt(sortValue.intValue())
                            .or(product.likeCount.eq(sortValue.intValue())
                                    .and(product.id.lt(id)))
            );

            // 최신 등록 순
            case DEFAULT -> builder.and(product.id.lt(id));
        }
    }

    private Cursor<Number> extractCursor(ProductSortType sortType, ProductItem item) {
        Number sortValue = switch (sortType) {
            case REVIEW_COUNT_DESC -> item.getReviewCount();
            case RATING_DESC, RATING_ASC -> item.getAverageRating();
            case LIKE_COUNT_DESC -> item.getLikeCount();
            case DEFAULT -> item.getProductId();
        };

        return new Cursor<>(sortValue, item.getProductId());
    }

    public List<Integer> findProductIdsByDefaultRanking(int topK) {
        return queryFactory
                .select(product.id)
                .from(product)
                .orderBy(product.likeCount.desc(), product.id.desc())
                .limit(topK)
                .fetch()
                .stream()
                .map(Long::intValue)
                .toList();
    }

    public List<ProductItem> searchByName(String keyword) {
        return queryFactory
                .select(Projections.constructor(
                        ProductItem.class,
                        product.id,
                        product.thumbnailImg,
                        brand.id,
                        brand.name,
                        product.name,
                        product.averageRating,
                        product.reviewCount,
                        product.likeCount,
                        product.costPrice
                ))
                .from(product)
                .join(product.brand, brand)
                .where(product.name.containsIgnoreCase(keyword))
                .orderBy(
                        new CaseBuilder()
                                .when(product.name.likeIgnoreCase(keyword + "%")).then(0)
                                .otherwise(1).asc(),
                        product.name.asc()
                )
                .limit(30)
                .fetch();
    }

    public Map<Long, List<String>> findIngredientLabelsByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return Map.of();

        List<Tuple> rows = queryFactory
                .select(
                        productIngredient.product.id,
                        ingredient.name,
                        ingredient.layerType
                )
                .from(productIngredient)
                .join(productIngredient.ingredient, ingredient)
                .where(productIngredient.product.id.in(productIds))
                .fetch();

        return rows.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(productIngredient.product.id),
                        Collectors.mapping(
                                tuple -> Ingredient.getDisplayLabel(
                                        tuple.get(ingredient.name),
                                        tuple.get(ingredient.layerType)
                                ),
                                Collectors.toList()
                        )
                ));
    }
}
