package smu.nuda.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryResponse {
    private String recipient;
    private String phoneNum;
    private String postalCode;
    private String address1;
    private String address2;
}
