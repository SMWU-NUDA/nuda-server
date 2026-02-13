package smu.nuda.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.brand.error.BrandErrorCode;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.product.error.ProductErrorCode;
import smu.nuda.domain.product.policy.ProductPolicy;
import smu.nuda.domain.product.util.ExternalProductIdGenerator;
import smu.nuda.global.error.DomainException;

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
            generator = "product_seq_generator"
    )
    @SequenceGenerator(
            name = "product_seq_generator",
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

    @Column(name = "external_product_id", nullable = false, updatable = false)
    private String externalProductId;

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

    public static Product create(String externalProductId, Brand brand, Category category, String name, int costPrice, int discountRate, String content, String thumbnailImg, double averageRating, int reviewCount) {
        if (brand == null) throw new DomainException(BrandErrorCode.INVALID_BRAND);
        if (category == null) throw new DomainException(ProductErrorCode.INVALID_CATEGORY);
        if (name == null || name.isBlank())
            throw new DomainException(ProductErrorCode.INVALID_PRODUCT_NAME);

        ProductPolicy.validateCreate(costPrice, discountRate, averageRating, reviewCount);

        Product product = new Product();
        if (externalProductId == null || externalProductId.isBlank())
            product.externalProductId = ExternalProductIdGenerator.generateInternalId();
        else product.externalProductId = externalProductId;
        product.brand = brand;
        product.category = category;
        product.name = name;
        product.costPrice = costPrice;
        product.discountRate = discountRate;
        product.content = content;
        product.thumbnailImg = thumbnailImg;
        product.averageRating = averageRating;
        product.reviewCount = reviewCount;

        return product;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

}
