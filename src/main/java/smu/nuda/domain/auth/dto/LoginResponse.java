package smu.nuda.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.member.dto.MeResponse;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private MeResponse meResponse;

}
