package smu.nuda.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.member.entity.Member;

@Getter
@AllArgsConstructor
public class MeResponse {
    private Long id;
    private String username;
    private String nickname;
    private String profileImg;
    private String email;

    public static MeResponse from(Member member) {
        return new MeResponse(
                member.getId(),
                member.getUsername(),
                member.getNickname(),
                member.getProfileImg(),
                member.getEmail()
        );
    }
}
