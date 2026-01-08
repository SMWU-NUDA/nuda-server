package smu.nuda.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.member.entity.enums.Role;
import smu.nuda.domain.member.entity.enums.SignupStepType;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.global.error.DomainException;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq")
    @SequenceGenerator(
            name = "member_seq",
            sequenceName = "member_seq",
            allocationSize = 1
    )
    private Long id;

    private String nickname;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "profile_img")
    private String profileImg;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "signup_step", nullable = false)
    private SignupStepType signupStep;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "postal_code")
    private String postalCode;

    private String address1;
    private String address2;

    @Column(name = "phone_num")
    private String phoneNum;

    private String recipient;

    public void updateDelivery(String recipient, String phoneNum, String postalCode, String address1, String address2) {
        this.recipient = recipient;
        this.phoneNum = phoneNum;
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void completeDelivery() {
        this.signupStep = SignupStepType.DELIVERY;
    }

    public void completeSurvey() {
        this.signupStep = SignupStepType.SURVEY;
    }

    public void completeSignup() {
        this.signupStep = SignupStepType.COMPLETED;
    }

    public void requestWithdraw() {
        if (this.status != Status.ACTIVE) {
            throw new DomainException(MemberErrorCode.INVALID_STATUS);
        }
        this.status = Status.WITHDRAW_REQUESTED;
    }

    public boolean isWithinWithdrawCooldown() {
        return false;
    }
}
