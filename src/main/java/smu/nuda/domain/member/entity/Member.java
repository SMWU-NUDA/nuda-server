package smu.nuda.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.nuda.domain.common.entity.BaseEntity;
import smu.nuda.domain.member.entity.enums.Role;
import smu.nuda.domain.member.entity.enums.Status;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Status status;

    @Column(name = "postal_code")
    private String postalCode;

    private String address1;
    private String address2;

    @Column(name = "phone_num")
    private String phoneNum;

    private String recipient;
}
