package smu.nuda.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.keyword.dto.KeywordResponse;

@Getter
@AllArgsConstructor
public class MyPageResponse {
    private MeResponse me;
    private KeywordResponse keyword;
}
