package smu.nuda.global.ml.dto;

import smu.nuda.domain.keyword.entity.enums.AdhesionLevel;
import smu.nuda.domain.keyword.entity.enums.IrritationLevel;
import smu.nuda.domain.keyword.entity.enums.ScentLevel;
import smu.nuda.domain.keyword.entity.enums.ThicknessLevel;

public record KeywordSyncRequest (
    Long memberId,
    IrritationLevel irritationLevel,
    ScentLevel scent,
    ThicknessLevel thickness,
    AdhesionLevel adhesion
){
}
