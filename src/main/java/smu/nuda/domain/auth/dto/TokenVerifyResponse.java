package smu.nuda.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.member.dto.MeResponse;
import smu.nuda.domain.member.entity.Member;

@Getter
@AllArgsConstructor
public class TokenVerifyResponse {
    private boolean authenticated;
    private MeResponse meResponse;

    public static TokenVerifyResponse from(Member member) {
        return new TokenVerifyResponse(true, MeResponse.from(member));
    }
}
