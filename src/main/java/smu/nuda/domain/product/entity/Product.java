package smu.nuda.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product",
        indexes = {
                @Index(name = "idx_product_brand_id", columnList = "brand_id"),
                @Index(name = "idx_product_category_id", columnList = "category_id")
        }
)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_seq"
    )
    @SequenceGenerator(
            name = "product_seq",
            sequenceName = "product_seq",
            allocationSize = 50
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String name;

    @Column(name = "cost_price", nullable = false)
    private int costPrice;

    @Column(name="discount_rate", nullable = false)
    private int discountRate;

//    @Column(nullable = false)
//    private int salePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "average_rating")
    private double averageRating = 0;

    @Column(name = "review_count")
    private int reviewCount = 0;

    @Column(name = "like_count")
    private int likeCount = 0;

    @Column(name = "sales_count")
    private int salesCount = 0;

    @Column(name = "view_count")
    private int viewCount = 0;

    @Column(name = "thumbnail_img")
    private String thumbnailImg;
}
