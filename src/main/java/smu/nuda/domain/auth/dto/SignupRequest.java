package smu.nuda.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String email;
    private String username;
    private String password; // Todo. 비밀번호 형식에 맞추기
    private String nickname;
}

