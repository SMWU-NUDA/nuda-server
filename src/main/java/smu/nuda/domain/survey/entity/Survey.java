package smu.nuda.domain.survey.entity;

import jakarta.persistence.*;
import lombok.*;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.signupdraft.entity.SignupDraft;
import smu.nuda.domain.survey.entity.enums.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "survey")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "survey_seq")
    @SequenceGenerator(
            name = "survey_seq",
            sequenceName = "survey_seq",
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
    private ChangeFrequency changeFrequency;

    @Enumerated(EnumType.STRING)
    private ThicknessLevel thickness;

    @Enumerated(EnumType.STRING)
    private PriorityType priority;

    public static Survey of(SignupDraft draft, Member member) {
        return Survey.builder()
                .member(member)
                .irritationLevel(draft.getIrritationLevel())
                .scent(draft.getScent())
                .changeFrequency(draft.getChangeFrequency())
                .thickness(draft.getThickness())
                .priority(draft.getPriority())
                .build();
    }
}
