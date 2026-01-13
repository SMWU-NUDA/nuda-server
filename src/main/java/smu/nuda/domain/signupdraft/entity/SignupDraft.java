package smu.nuda.domain.signupdraft.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.domain.survey.entity.enums.*;

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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nickname;

    @Column(name = "postal_code")
    private String postalCode;

    private String address1;
    private String address2;

    @Column(name = "phone_num")
    private String phoneNum;

    private String recipient;

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

    @Column(name = "product_ids", columnDefinition = "TEXT")
    private String productIds;

    public static SignupDraft create(String signupToken) {
        SignupDraft draft = new SignupDraft();
        draft.signupToken = signupToken;
        draft.currentStep = SignupStep.ACCOUNT;
        return draft;
    }

    public void updateAccount(String email, String encodedPassword, String nickname, String username) {
        this.email = email;
        this.username = username;
        this.password = encodedPassword;
        this.nickname = nickname;

        this.currentStep = SignupStep.DELIVERY;
    }

    public void updateDelivery(String recipient, String phoneNum, String postalCode, String address1, String address2) {
        this.recipient = recipient;
        this.phoneNum = phoneNum;
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;

        this.currentStep = SignupStep.SURVEY;
    }

    public void updateSurvey(IrritationLevel irritationLevel, ScentLevel scent, ChangeFrequency changeFrequency, ThicknessLevel thickness, PriorityType priority, String productIds) {
        this.irritationLevel = irritationLevel;
        this.scent = scent;
        this.changeFrequency = changeFrequency;
        this.thickness = thickness;
        this.priority = priority;
        this.productIds = productIds;

        this.currentStep = SignupStep.COMPLETED;
    }

}
