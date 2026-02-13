package smu.nuda.domain.keyword.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smu.nuda.domain.keyword.entity.Keyword;

import java.util.List;

@Getter
@AllArgsConstructor
public class KeywordResponse {

    private List<String> keywords;

    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(
                List.of(
                        keyword.getIrritationLevel().getLabel(),
                        keyword.getScent().getLabel(),
                        keyword.getChangeFrequency().getLabel(),
                        keyword.getThickness().getLabel(),
                        keyword.getPriority().getLabel()
                )
        );
    }
}
