package smu.nuda.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedBrandResponse {
    private Long likeId;
    private Long brandId;

    private String logoImg;
    private String name;
}
