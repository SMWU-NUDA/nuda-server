package smu.nuda.domain.like.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.ingredient.entity.Ingredient;
import smu.nuda.domain.member.entity.Member;

@Entity
@Table(
        name = "ingredient_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_member_ingredient_like",
                        columnNames = {"member_id", "ingredient_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_ingredient_like_member_ingredient",
                        columnList = "member_id, ingredient_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "ingredient_like_seq_generator",
        sequenceName = "ingredient_like_seq",
        allocationSize = 1
)
public class IngredientLike extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ingredient_like_seq_generator"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ingredient_like_member")
    )
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ingredient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ingredient_like_ingredient")
    )
    private Ingredient ingredient;

    @Column(nullable = false)
    private boolean preference;

    public static IngredientLike prefer(Member member, Ingredient ingredient) {
        IngredientLike il = new IngredientLike();
        il.member = member;
        il.ingredient = ingredient;
        il.preference = true;
        return il;
    }

    public static IngredientLike avoid(Member member, Ingredient ingredient) {
        IngredientLike il = new IngredientLike();
        il.member = member;
        il.ingredient = ingredient;
        il.preference = false;
        return il;
    }

    public void updatePreference(boolean preference) {
        this.preference = preference;
    }


}
