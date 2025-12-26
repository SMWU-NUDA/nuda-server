package smu.nuda.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.member.entity.enums.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "survey")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
