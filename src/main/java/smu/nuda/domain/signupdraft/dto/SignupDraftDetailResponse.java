package smu.nuda.domain.signupdraft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;

@Getter
@AllArgsConstructor
@Builder
public class SignupDraftDetailResponse {
    private SignupStep currentStep;
    private AccountInfo accountInfo;
    private DeliveryInfo deliveryInfo;
    private KeywordInfo keywordInfo;
    private String expiresAt;
}
