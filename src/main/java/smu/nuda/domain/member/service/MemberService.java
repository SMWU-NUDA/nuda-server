package smu.nuda.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.auth.repository.EmailAuthRepository;
import smu.nuda.domain.member.dto.DeliveryRequest;
import smu.nuda.domain.member.dto.DeliveryResponse;
import smu.nuda.domain.member.dto.MeResponse;
import smu.nuda.domain.member.dto.UpdateMemberRequest;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.error.DomainException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MeResponse updateMe(Member authmember, UpdateMemberRequest request) {

        Member member = memberRepository.findById(authmember.getId())
                .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (request.getUsername() != null) {
            member.updateUsername(request.getUsername());
        }

        if (request.getNickname() != null) {
            member.updateNickname(request.getNickname());
        }

        if (request.getEmail() != null) {
            if (!emailAuthRepository.isVerified(request.getEmail())) {
                throw new DomainException(AuthErrorCode.EMAIL_NOT_VERIFIED);
            }

            member.updateEmail(request.getEmail());
            emailAuthRepository.clearVerified(request.getEmail());
        }

        if (request.getNewPassword() != null) {
            if (request.getCurrentPassword() == null) {
                throw new DomainException(MemberErrorCode.PASSWORD_REQUIRED);
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
                throw new DomainException(MemberErrorCode.INVALID_PASSWORD);
            }

            member.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return MeResponse.from(member);
    }

    @Transactional
    public DeliveryResponse updateDelivery(Member authMember, DeliveryRequest request) {
        Member member = memberRepository.findById(authMember.getId())
                .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateDelivery(
                request.getRecipient(),
                request.getPhoneNum(),
                request.getPostalCode(),
                request.getAddress1(),
                request.getAddress2()
        );

        return DeliveryResponse.builder()
                .recipient(member.getRecipient())
                .phoneNum(member.getPhoneNum())
                .postalCode(member.getPostalCode())
                .address1(member.getAddress1())
                .address2(member.getAddress2())
                .build();
    }


}
