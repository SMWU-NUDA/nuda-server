package smu.nuda.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueResponse {
    private final String accessToken;
    private final String refreshToken;
}
