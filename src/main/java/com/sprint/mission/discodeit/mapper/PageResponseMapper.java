package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.page.PageOffsetResponse;
import com.sprint.mission.discodeit.dto.response.page.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {
    default <T> PageOffsetResponse<T> fromSliceToOffset(Slice<T> slice) {
        return PageOffsetResponse.<T>builder()
                .content(slice.getContent())
                .number(slice.getNumber())
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .totalElements(null) // Slice는 총 개수를 모름
                .build();
    }

    default <T> PageOffsetResponse<T> fromPageToOffset(Page<T> page) {
        return PageOffsetResponse.<T>builder()
                .content(page.getContent())
                .number(page.getNumber())
                .size(page.getSize())
                .hasNext(page.hasNext())
                .totalElements(page.getTotalElements())
                .build();
    }

    default <T> PageResponse<T> fromSliceToCursor(List<T> content, Object nextCursor, int size, boolean hasNext) {
        return PageResponse.<T>builder()
                .content(content)
                .nextCursor(nextCursor)
                .size(size)
                .hasNext(hasNext)
                .totalElements(null) // 커서 기반이므로 전체 개수는 계산 안함
                .build();
    }

    default <T> PageResponse<T> fromPageToCursor(Page<T> page, Object nextCursor) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .nextCursor(nextCursor)
                .size(page.getSize())
                .hasNext(page.hasNext())
                .totalElements(page.getTotalElements())
                .build();
    }

    default <T> PageResponse<T> fromListToCursor(List<T> content, Object nextCursor, int size, boolean hasNext) {
        return PageResponse.<T>builder()
                .content(content)
                .nextCursor(nextCursor)
                .size(size)
                .hasNext(hasNext)
                .totalElements(null) // 커서 기반은 전체 개수 모름
                .build();
    }
}
