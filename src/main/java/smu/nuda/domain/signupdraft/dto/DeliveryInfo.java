package smu.nuda.domain.signupdraft.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryInfo {
    private String postalCode;
    private String address1;
    private String address2;
    private String recipient;
    private String phoneNum;
}
