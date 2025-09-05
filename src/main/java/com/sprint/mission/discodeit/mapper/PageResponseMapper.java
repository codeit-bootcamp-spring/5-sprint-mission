package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.page.PageOffsetResponse;
import com.sprint.mission.discodeit.dto.response.page.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

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

    default <T> PageResponse<T> fromSliceToCursor(Slice<T> slice, Object nextCursor) {
        return PageResponse.<T>builder()
                .content(slice.getContent())
                .nextCursor(nextCursor)
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .totalElements(null)
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
}
