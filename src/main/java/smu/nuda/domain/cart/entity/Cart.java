package smu.nuda.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.cart.error.CartErrorCode;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.error.DomainException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(
        name = "cart",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cart_member", columnNames = "member_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cart_seq_generator"
    )
    @SequenceGenerator(
            name = "cart_seq_generator",
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
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) { // 이미 존재하면 수량만 증가
            existingItem.get().increaseQuantity(1);
            return existingItem.get().getQuantity();
        } else { // 없으면 새로 생성 -> 수량 1개
            CartItem newItem = new CartItem(productId, 1);
            addItem(newItem);
            return newItem.getQuantity();
        }
    }

    void addItem(CartItem item) {
        items.add(item);
        item.assignCart(this);
    }

    void removeItem(CartItem item) {
        items.remove(item);
        item.removeCart();
    }

    public void validateOwner(Member member) {
        if (member == null) {
            throw new DomainException(CartErrorCode.NOT_MY_CART_ITEM);
        }

        if (!this.memberId.equals(member.getId())) {
            throw new DomainException(CartErrorCode.NOT_MY_CART_ITEM);
        }
    }
}
