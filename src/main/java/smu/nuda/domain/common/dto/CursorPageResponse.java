package smu.nuda.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class CursorPageResponse<T> {

    private List<T> content;
    private Long nextCursor;
    private boolean hasNext;

    public static <T> CursorPageResponse<T> of(List<T> content, int size, Function<T, Long> cursorExtractor) {
        if (content.size() < size) return new CursorPageResponse<>(content, null, false);

        T last = content.get(size - 1);
        Long nextCursor = cursorExtractor.apply(last);

        return new CursorPageResponse<>(
                content.subList(0, size),
                nextCursor,
                true
        );
    }
}
