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

    public static <T> CursorPageResponse<T> of(List<T> slicedContent, int nextIndex, int totalSize) {
        boolean hasNext = nextIndex < totalSize;

        return new CursorPageResponse<>(
                slicedContent,
                hasNext ? (long) nextIndex : null,
                hasNext
        );
    }

    public static <T> CursorPageResponse<T> sliceFromIndex(List<T> fullList, Long cursor, int size) {
        int start = cursor == null ? 0 : cursor.intValue();
        if (start >= fullList.size()) return new CursorPageResponse<>(List.of(), null, false);
        int end = Math.min(start + size, fullList.size());
        List<T> sliced = fullList.subList(start, end);

        boolean hasNext = end < fullList.size();
        Long nextCursor = hasNext ? (long) end : null;

        return new CursorPageResponse<>(sliced, nextCursor, hasNext);
    }
}
