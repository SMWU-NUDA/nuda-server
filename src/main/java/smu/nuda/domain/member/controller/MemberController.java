package smu.nuda.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.member.dto.ShippingInfoRequest;
import smu.nuda.domain.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/{memberId}/shipping")
    public void updateShippingInfo(@PathVariable Long memberId, @RequestBody ShippingInfoRequest request) {
        memberService.updateShippingInfo(memberId, request);
    }
}
