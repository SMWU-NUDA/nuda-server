package smu.nuda.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.auth.repository.EmailAuthRepository;
import smu.nuda.domain.member.dto.*;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.survey.dto.SurveyKeywordResponse;
import smu.nuda.domain.survey.repository.SurveyRepository;
import smu.nuda.global.error.DomainException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final SurveyRepository surveyRepository;

    @Transactional
    public MeResponse updateMe(Member member, UpdateMemberRequest request) {
        if (request.getUsername() != null) {
            member.updateUsername(request.getUsername());
        }

        if (request.getNickname() != null) {
            member.updateNickname(request.getNickname());
        }

        if (request.getEmail() != null) {
            if (!emailAuthRepository.isVerified(request.getEmail())) {
                throw new DomainException(MemberErrorCode.EMAIL_NOT_VERIFIED);
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

    @Transactional(readOnly = true)
    public DeliveryResponse getDelivery(Member member) {
        return DeliveryResponse.builder()
                .recipient(member.getRecipient())
                .phoneNum(member.getPhoneNum())
                .postalCode(member.getPostalCode())
                .address1(member.getAddress1())
                .address2(member.getAddress2())
                .build();
    }


    @Transactional
    public DeliveryResponse updateDelivery(Member member, DeliveryRequest request) {
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

    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(Member member) {
        MeResponse me = MeResponse.from(member);
        SurveyKeywordResponse survey = surveyRepository.findByMember(member)
                .map(SurveyKeywordResponse::from)
                .orElse(null);

        return new MyPageResponse(me, survey);
    }

}
