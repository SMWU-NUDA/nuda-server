package smu.nuda.domain.cart.service;

import com.querydsl.core.Tuple;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import org.hibernate.PessimisticLockException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.cart.dto.CartBrandInfo;
import smu.nuda.domain.cart.dto.CartProductInfo;
import smu.nuda.domain.cart.dto.CartProductResponse;
import smu.nuda.domain.cart.dto.CartResponse;
import smu.nuda.domain.cart.entity.Cart;
import smu.nuda.domain.cart.error.CartErrorCode;
import smu.nuda.domain.cart.repository.CartQueryRepository;
import smu.nuda.domain.cart.repository.CartRepository;
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
    public CartResponse getCart(Long memberId) {
        List<Tuple> rows = cartQueryRepository.findCartProducts(memberId);

        Map<Long, List<Tuple>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> row.get(brand.id),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        List<CartBrandInfo> brands = grouped.values().stream()
                .map(group -> {
                    Tuple first = group.get(0);

                    Long brandId = first.get(brand.id);
                    String brandName = first.get(brand.name);

                    List<CartProductInfo> products = group.stream()
                            .map(row -> {
                                Long productId = row.get(product.id);
                                String productName = row.get(product.name);
                                int quantity = row.get(cartItem.quantity);
                                int price = row.get(product.costPrice);

                                return new CartProductInfo(
                                        productId,
                                        productName,
                                        quantity,
                                        price,
                                        quantity * price
                                );
                            }).toList();

                    return new CartBrandInfo(
                            brandId,
                            brandName,
                            products
                    );
                }).toList();

        int totalQuantity = rows.stream()
                .mapToInt(row -> row.get(cartItem.quantity)).sum();
        int totalPrice = rows.stream()
                .mapToInt(row -> row.get(cartItem.quantity) * row.get(product.costPrice)).sum();

        return new CartResponse(brands, totalQuantity, totalPrice);
    }
    
}
