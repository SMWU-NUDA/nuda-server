package smu.nuda.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeToggleResponse {

    private boolean liked;
    private int likeCount;

    public static LikeToggleResponse liked(int count) {
        return new LikeToggleResponse(true, count);
    }

    public static LikeToggleResponse unliked(int count) {
        return new LikeToggleResponse(false, count);
    }

}
