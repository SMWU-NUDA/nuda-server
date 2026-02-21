package smu.nuda.domain.keyword.entity;

import jakarta.persistence.*;
import lombok.*;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.keyword.entity.enums.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "keyword")
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "keyword_seq_generator"
    )
    @SequenceGenerator(
            name = "keyword_seq_generator",
            sequenceName = "keyword_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private IrritationLevel irritationLevel;

    @Enumerated(EnumType.STRING)
    private ScentLevel scent;

    @Enumerated(EnumType.STRING)
    private AdhesionLevel adhesion;

    @Enumerated(EnumType.STRING)
    private ThicknessLevel thickness;

    @Enumerated(EnumType.STRING)
    private ChangeFrequency changeFrequency;

    public static Keyword of(SignupDraft draft, Member member) {
        return Keyword.builder()
                .member(member)
                .irritationLevel(draft.getIrritationLevel())
                .scent(draft.getScent())
                .adhesion(draft.getAdhesion())
                .thickness(draft.getThickness())
                .changeFrequency(draft.getChangeFrequency())
                .build();
    }

    public List<String> getLabels() {
        return List.of(
                irritationLevel.getLabel(),
                scent.getLabel(),
                adhesion.getLabel(),
                thickness.getLabel()
        );
    }

    public void update(IrritationLevel irritationLevel, ScentLevel scent, AdhesionLevel adhesion, ThicknessLevel thickness) {
        if (irritationLevel != null) this.irritationLevel = irritationLevel;
        if (scent != null) this.scent = scent;
        if (adhesion != null) this.adhesion = adhesion;
        if (thickness != null) this.thickness = thickness;
    }

}
