package smu.nuda.domain.signupdraft.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;

@Entity
@Table(name = "signup_draft")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupDraft extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "signup_draft_seq"
    )
    @SequenceGenerator(
            name = "signup_draft_seq",
            sequenceName = "signup_draft_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "signup_token", nullable = false, unique = true, length = 64)
    private String signupToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false, length = 30)
    private SignupStep currentStep;

    public static SignupDraft create(String signupToken) {
        SignupDraft draft = new SignupDraft();
        draft.signupToken = signupToken;
        draft.currentStep = SignupStep.BASIC;
        return draft;
    }
}
