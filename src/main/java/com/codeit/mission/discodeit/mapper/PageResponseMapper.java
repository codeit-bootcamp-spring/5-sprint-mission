package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PageResponseMapper {

    public static <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return new PageResponse<>(
            slice.getContent(),
            slice.getNumber(),
            slice.getSize(),
            slice.hasNext(),
            null
        );
    }

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.hasNext(),
            page.getTotalElements()
        );
    }
}
