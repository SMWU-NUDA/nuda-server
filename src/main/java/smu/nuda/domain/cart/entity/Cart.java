package smu.nuda.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "cart",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cart_member", columnNames = "member_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cart_seq"
    )
    @SequenceGenerator(
            name = "cart_seq",
            sequenceName = "cart_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartItem> items = new ArrayList<>();

    public Cart(Long memberId) {
        this.memberId = memberId;
    }

    public int addProduct(Long productId) {
        CartItem cartItem = items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem(productId,1);
                    items.add(newItem);
                    return newItem;
                });

        cartItem.increaseQuantity(1);
        return cartItem.getQuantity();
    }

    void addItem(CartItem item) {
        items.add(item);
        item.assignCart(this);
    }

    void removeItem(CartItem item) {
        items.remove(item);
        item.removeCart();
    }
}
