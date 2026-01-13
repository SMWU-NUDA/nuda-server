package smu.nuda.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DeliveryRequest {
    @NotBlank
    private String recipient;
    @NotBlank
    private String phoneNum;
    @NotBlank
    private String postalCode;
    @NotBlank
    private String address1;
    private String address2;
}
