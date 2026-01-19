package smu.nuda.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordVerifyRequest {
    @NotBlank private String password;
}
