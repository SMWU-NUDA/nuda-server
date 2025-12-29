package smu.nuda.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.dto.ShippingInfoRequest;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.error.DomainException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void updateShippingInfo(Long memberId, ShippingInfoRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateShippingInfo(
                request.getRecipient(),
                request.getPhoneNum(),
                request.getPostalCode(),
                request.getAddress1(),
                request.getAddress2()
        );
    }
}
