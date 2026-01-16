package smu.nuda.domain.signupdraft.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountRequest {
    @NotBlank  private String email;
    @NotBlank private String username;
    @NotBlank private String password; // Todo. 비밀번호 형식에 맞추기
    @NotBlank private String nickname;
}
