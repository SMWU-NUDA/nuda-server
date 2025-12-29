package smu.nuda.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeliveryRequest {
    private String recipient;
    private String phoneNum;
    private String postalCode;
    private String address1;
    private String address2;
}
