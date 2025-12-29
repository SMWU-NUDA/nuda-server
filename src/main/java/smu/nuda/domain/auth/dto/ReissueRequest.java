package smu.nuda.domain.auth.dto;

import lombok.Getter;

@Getter
public class ReissueRequest {
    private String refreshToken;
}
