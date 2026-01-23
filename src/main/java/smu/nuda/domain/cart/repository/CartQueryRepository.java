package smu.nuda.domain.cart.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static smu.nuda.domain.cart.entity.QCart.cart;
import static smu.nuda.domain.cart.entity.QCartItem.cartItem;
import static smu.nuda.domain.product.entity.QProduct.product;
import static smu.nuda.domain.brand.entity.QBrand.brand;

@Repository
@RequiredArgsConstructor
public class CartQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Tuple> findCartProducts(Long memberId) {

        return queryFactory
                .select(
                        cartItem.id,
                        brand.id,
                        brand.name,
                        product.id,
                        product.name,
                        cartItem.quantity,
                        product.costPrice
                )
                .from(cartItem)
                .join(cartItem.cart, cart)
                .join(product).on(product.id.eq(cartItem.productId))
                .join(product.brand, brand)
                .where(cart.memberId.eq(memberId))
                .fetch();
    }
}

