package smu.nuda.domain.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class SurveyProductRequest {
    private List<Long> productIds;
}
