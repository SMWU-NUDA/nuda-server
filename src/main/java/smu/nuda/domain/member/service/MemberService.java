package smu.nuda.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.auth.repository.EmailAuthRedisRepository;
import smu.nuda.domain.keyword.entity.Keyword;
import smu.nuda.domain.keyword.error.KeywordErrorCode;
import smu.nuda.domain.member.dto.*;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.keyword.repository.KeywordRepository;
import smu.nuda.global.error.DomainException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final EmailAuthRedisRepository emailAuthRedisRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeywordRepository keywordRepository;

    @Transactional
    public MeResponse updateMe(Member member, UpdateMemberRequest request) {
        if (request.getUsername() != null) {
            member.updateUsername(request.getUsername());
        }

        if (request.getNickname() != null) {
            member.updateNickname(request.getNickname());
        }

        if (request.getEmail() != null) {
            if (!emailAuthRedisRepository.isVerified(request.getEmail())) {
                throw new DomainException(MemberErrorCode.EMAIL_NOT_VERIFIED);
            }

            member.updateEmail(request.getEmail());
            emailAuthRedisRepository.clearVerified(request.getEmail());
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
        return DeliveryResponse.from(member);
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

        return DeliveryResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(Member member) {
        MeResponse me = MeResponse.from(member);
        Keyword keyword = keywordRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new DomainException(KeywordErrorCode.KEYWORD_NOT_FOUND));

        return new MyPageResponse(me, keyword.getLabels());
    }

}
