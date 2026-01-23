package smu.nuda.domain.signupdraft.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountInfo {
    private String email;
    private String username;
    private String nickname;
}
