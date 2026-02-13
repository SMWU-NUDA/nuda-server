package smu.nuda.domain.brand.entity;

import jakarta.persistence.*;
import lombok.*;
import smu.nuda.domain.common.entity.BaseEntity;

@Entity
@Table(name = "brand")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "brand_seq_generator"
    )
    @SequenceGenerator(
            name = "brand_seq_generator",
            sequenceName = "brand_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String logoImg;

    @Column(name = "like_count")
    private int likeCount = 0;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

}
