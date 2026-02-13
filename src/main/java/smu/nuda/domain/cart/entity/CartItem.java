package smu.nuda.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.cart.error.CartErrorCode;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.global.error.DomainException;

@Entity
@Table(
        name = "cart_item",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_item_cart_product",
                        columnNames = {"cart_id", "product_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cart_item_seq_generator"
    )
    @SequenceGenerator(
            name = "cart_item_seq_generator",
            sequenceName = "cart_item_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cart_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_item_cart")
    )
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    public CartItem(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new DomainException(CartErrorCode.INVALID_QUANTITY);
        }
        this.productId = productId;
        this.quantity = quantity;
    }

    void assignCart(Cart cart) {
        this.cart = cart;
    }

    void removeCart() {
        this.cart = null;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) throw new DomainException(CartErrorCode.INVALID_QUANTITY);
        this.quantity += amount;
    }

    public void changeQuantity(int delta) {
        int newQuantity = this.quantity + delta;
        if (newQuantity < 1) {
            throw new DomainException(CartErrorCode.INVALID_QUANTITY);
        }
        this.quantity = newQuantity;
    }

}
