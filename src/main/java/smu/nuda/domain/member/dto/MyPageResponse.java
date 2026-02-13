package smu.nuda.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyPageResponse {
    private MeResponse me;
    private List<String> keywords;

}
