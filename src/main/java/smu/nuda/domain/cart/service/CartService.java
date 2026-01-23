package smu.nuda.domain.cart.service;

import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import org.hibernate.PessimisticLockException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.cart.dto.CartItemResponse;
import smu.nuda.domain.cart.entity.Cart;
import smu.nuda.domain.cart.error.CartErrorCode;
import smu.nuda.domain.cart.repository.CartRepository;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.error.DomainException;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartItemResponse addProduct(Long memberId, Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DomainException(ProductErrorCode.INVALID_PRODUCT));
            Cart cart = cartRepository.findByMemberIdForUpdate(memberId)
                    .orElseThrow(() -> new DomainException(CartErrorCode.CART_NOT_FOUND));

            return CartItemResponse.builder()
                    .productId(productId)
                    .quantity(cart.addProduct(productId))
                    .build();
        } catch (PessimisticLockException | LockTimeoutException | CannotAcquireLockException e) {
            throw new DomainException(CartErrorCode.LOCK_TIMEOUT);
        }
    }

}
