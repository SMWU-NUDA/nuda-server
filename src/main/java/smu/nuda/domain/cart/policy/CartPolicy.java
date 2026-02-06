package smu.nuda.domain.cart.policy;

import smu.nuda.domain.cart.entity.CartItem;
import smu.nuda.domain.cart.error.CartErrorCode;
import smu.nuda.domain.order.dto.OrderItemRequest;
import smu.nuda.global.error.DomainException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CartPolicy {

    public static void validateOrderableItems(List<CartItem> cartItems, List<OrderItemRequest> orderItems) {
        Map<Long, CartItem> cartItemMap = cartItems.stream()
                .collect(Collectors.toMap(
                        CartItem::getProductId,
                        Function.identity()
                ));

        for (OrderItemRequest orderItem : orderItems) {
            CartItem cartItem = cartItemMap.get(orderItem.getProductId());

            if (cartItem == null) throw new DomainException(CartErrorCode.INVALID_CART_ITEM);
            if (cartItem.getQuantity() != orderItem.getQuantity()) throw new DomainException(CartErrorCode.QUANTITY_MISMATCH);
        }
    }
}

