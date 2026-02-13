package smu.nuda.domain.like.entity;

import jakarta.persistence.*;
import lombok.*;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.product.entity.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "product_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_like_member_product",
                        columnNames = {"member_id", "product_id"}
                )
        }
)
public class ProductLike extends BaseEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_like_seq_generator"
    )
    @SequenceGenerator(
            name = "product_like_seq_generator",
            sequenceName = "product_like_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_like_member")
    )
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_like_product")
    )
    private Product product;

    public ProductLike(Member member, Product product) {
        this.member = member;
        this.product = product;
    }

}
