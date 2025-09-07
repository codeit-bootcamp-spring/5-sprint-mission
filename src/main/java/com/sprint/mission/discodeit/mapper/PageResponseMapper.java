package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

    public <T, R> PageResponse<R> fromSlice(Slice<T> slice, Function<T, R> mapper) {
        List<R> content = slice.getContent().stream()
            .map(mapper)
            .collect(Collectors.toList());
        return new PageResponse<>(
            content,
            slice.getNumber(),
            slice.getSize(),
            slice.hasNext(),
            null // Slice라면 totalElements는 모름
        );
    }

    public <T, R> PageResponse<R> fromPage(Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream()
            .map(mapper)
            .collect(Collectors.toList());
        return new PageResponse<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.hasNext(),
            page.getTotalElements()
        );
    }
}
