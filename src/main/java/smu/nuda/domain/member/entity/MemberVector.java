package smu.nuda.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "member_vector")
public class MemberVector extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_vector_seq")
    @SequenceGenerator(
            name = "member_vector_seq",
            sequenceName = "member_vector_seq",
            allocationSize = 50
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(columnDefinition = "vector")
    private String preferenceVector;

    private String version;
}
