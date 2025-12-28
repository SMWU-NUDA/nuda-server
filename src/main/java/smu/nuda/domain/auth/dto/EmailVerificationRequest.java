package smu.nuda.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerificationRequest {
    private String email;

    public String getEmail() {
        return email;
    }
}
