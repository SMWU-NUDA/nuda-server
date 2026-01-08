package smu.nuda.domain.like.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "brand_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_brand_like_member_brand",
                        columnNames = {"member_id", "brand_id"}
                )
        }
)
public class BrandLike extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "brand_like_seq"
    )
    @SequenceGenerator(
            name = "brand_like_seq",
            sequenceName = "brand_like_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_brand_like_member")
    )
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "brand_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_brand_like_brand")
    )
    private Brand brand;

    public BrandLike(Member member, Brand brand) {
        this.member = member;
        this.brand = brand;
    }

}