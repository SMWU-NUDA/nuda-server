package smu.nuda.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.product.entity.enums.Category;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int cost;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String content;

    private double averageRating;
    private int reviewCount;
    private int likeCount;
    private int salesCount;
    private int viewCount;

    @Column(name = "thumbnail_img")
    private String thumbnailImg;
}
