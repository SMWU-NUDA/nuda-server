package smu.nuda.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class CursorResponse<T, V> {
    private List<T> content;
    private Cursor<V> nextCursor;
    private boolean hasNext;

    public static <T, V> CursorResponse<T, V> of(List<T> content, int size, Function<T, Cursor<V>> cursorExtractor) {
        boolean hasNext = content.size() > size;
        List<T> data = hasNext ? content.subList(0, size) : content;

        Cursor<V> nextCursor = null;
        if (hasNext && !data.isEmpty()) {
            nextCursor = cursorExtractor.apply(data.get(data.size() - 1));
        }

        return new CursorResponse<>(data, nextCursor, hasNext);
    }
}
