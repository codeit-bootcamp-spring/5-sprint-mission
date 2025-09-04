package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.function.Function;

@Mapper(componentModel ="spring")
public interface PageResponseMapper {
    PageResponseMapper INSTANCE = Mappers.getMapper(PageResponseMapper.class);

    default <T> PageResponse<T> fromSlice(Slice<T> slice, Function<T, Object> cursorExtractor) {
        List<T> content = slice.getContent();

        boolean hasNext = slice.hasNext();
        Object nextCursor = hasNext && !content.isEmpty()
                ? cursorExtractor.apply(content.get(content.size() - 1))
                : null;

        return PageResponse.<T>builder()
                .content(content)
                .nextCursor(nextCursor)
                .size(slice.getSize())
                .hasNext(hasNext)
                .totalElements(null) // Slice는 total count 없음
                .build();
    }

    default <T> PageResponse<T> fromPage(Page<T> page, Function<T, Object> cursorExtractor) {
        List<T> content = page.getContent();

        boolean hasNext = page.hasNext();
        Object nextCursor = hasNext && !content.isEmpty()
                ? cursorExtractor.apply(content.get(content.size() - 1))
                : null;

        return PageResponse.<T>builder()
                .content(content)
                .nextCursor(nextCursor)
                .size(page.getSize())
                .hasNext(hasNext)
                .totalElements(page.getTotalElements()) // Page는 total count 있음
                .build();
    }

}
