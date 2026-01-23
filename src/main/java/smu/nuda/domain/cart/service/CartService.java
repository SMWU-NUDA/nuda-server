package smu.nuda.domain.cart.service;

import com.querydsl.core.Tuple;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import org.hibernate.PessimisticLockException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.cart.dto.CartBrandGroup;
import smu.nuda.domain.cart.dto.CartProductItem;
import smu.nuda.domain.cart.dto.CartProductResponse;
import smu.nuda.domain.cart.dto.CartResponse;
import smu.nuda.domain.cart.entity.Cart;
import smu.nuda.domain.cart.entity.CartItem;
import smu.nuda.domain.cart.error.CartErrorCode;
import smu.nuda.domain.cart.repository.CartItemRepository;
import smu.nuda.domain.cart.repository.CartQueryRepository;
import smu.nuda.domain.cart.repository.CartRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.error.DomainException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static smu.nuda.domain.brand.entity.QBrand.brand;
import static smu.nuda.domain.product.entity.QProduct.product;
import static smu.nuda.domain.cart.entity.QCartItem.cartItem;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartQueryRepository cartQueryRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartProductResponse addProduct(Long memberId, Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DomainException(ProductErrorCode.INVALID_PRODUCT));
            Cart cart = cartRepository.findByMemberIdForUpdate(memberId)
                    .orElseThrow(() -> new DomainException(CartErrorCode.CART_NOT_FOUND));

            return CartProductResponse.builder()
                    .productId(productId)
                    .quantity(cart.addProduct(productId))
                    .build();
        } catch (PessimisticLockException | LockTimeoutException | CannotAcquireLockException e) {
            throw new DomainException(CartErrorCode.LOCK_TIMEOUT);
        }
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Member member) {
        List<Tuple> rows = cartQueryRepository.findCartProducts(member.getId());

        Map<Long, List<Tuple>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> row.get(brand.id),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        List<CartBrandGroup> brands = grouped.values().stream()
                .map(group -> {
                    Tuple first = group.get(0);

                    Long brandId = first.get(brand.id);
                    String brandName = first.get(brand.name);

                    List<CartProductItem> products = group.stream()
                            .map(row -> {
                                Long cartItemId = row.get(cartItem.id);
                                Long productId = row.get(product.id);
                                String productName = row.get(product.name);
                                int quantity = row.get(cartItem.quantity);
                                int price = row.get(product.costPrice);

                                return new CartProductItem(
                                        cartItemId,
                                        productId,
                                        productName,
                                        quantity,
                                        price,
                                        quantity * price
                                );
                            }).toList();

                    return new CartBrandGroup(
                            brandId,
                            brandName,
                            products
                    );
                }).toList();

        int totalQuantity = 0;
        int totalPrice = 0;
        for (Tuple row : rows) {
            int quantity = row.get(cartItem.quantity);
            int price = row.get(product.costPrice);

            totalQuantity += quantity;
            totalPrice += quantity * price;
        }

        return new CartResponse(brands, totalQuantity, totalPrice);
    }

    @Transactional
    public CartProductResponse changeQuantity(Long cartItemId, int delta, Member member) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new DomainException(CartErrorCode.INVALID_CART_ITEM));

        cartItem.getCart().validateOwner(member);

        if (delta < 0 && cartItem.getQuantity() == 1) {
            cartItemRepository.delete(cartItem);

            return CartProductResponse.builder()
                    .productId(cartItem.getProductId())
                    .quantity(0)
                    .build();
        }

        cartItem.changeQuantity(delta);

        return CartProductResponse.builder()
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .build();
    }

}
