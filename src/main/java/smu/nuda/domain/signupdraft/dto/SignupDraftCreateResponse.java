package smu.nuda.domain.signupdraft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;

@Getter
@AllArgsConstructor
@Builder
public class SignupDraftCreateResponse {
    private String signupToken;
    private SignupStep currentStep;
}
