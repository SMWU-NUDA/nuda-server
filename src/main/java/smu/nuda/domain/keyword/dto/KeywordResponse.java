package smu.nuda.domain.keyword.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.keyword.entity.Keyword;
import smu.nuda.domain.keyword.entity.enums.*;
import smu.nuda.domain.member.dto.MeResponse;
import smu.nuda.domain.member.entity.Member;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeywordResponse {

    private MeResponse me;
    private IrritationLevel irritationLevel;
    private ScentLevel scent;
    private AdhesionLevel adhesion;
    private ThicknessLevel thickness;

    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(
                null,
                keyword.getIrritationLevel(),
                keyword.getScent(),
                keyword.getAdhesion(),
                keyword.getThickness()
        );
    }

    public static KeywordResponse of(Keyword keyword, Member member) {
        return new KeywordResponse(
                MeResponse.from(member),
                keyword.getIrritationLevel(),
                keyword.getScent(),
                keyword.getAdhesion(),
                keyword.getThickness()
        );
    }
}
