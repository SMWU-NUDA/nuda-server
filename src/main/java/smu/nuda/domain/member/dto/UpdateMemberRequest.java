package smu.nuda.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateMemberRequest {
    @Size(min = 2, max = 10)
    private String username;
    @Size(min = 2, max = 10)
    private String nickname;
    @Email
    private String email;
    private String currentPassword;
    private String newPassword; // Todo. 비밀번호 형식 검증
}
