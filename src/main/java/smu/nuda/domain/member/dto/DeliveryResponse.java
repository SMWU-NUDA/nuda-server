package smu.nuda.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.nuda.domain.member.entity.Member;

@Getter
@AllArgsConstructor
public class DeliveryResponse {
    private String recipient;
    private String phoneNum;
    private String postalCode;
    private String address1;
    private String address2;

    public static DeliveryResponse from(Member member) {
        return new DeliveryResponse(
                member.getRecipient(),
                member.getPhoneNum(),
                member.getPostalCode(),
                member.getAddress1(),
                member.getAddress2()
        );
    }
}
