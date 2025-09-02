package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {

    default <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return PageResponse.<T>builder()
                .content(slice.getContent())
                .number(slice.getNumber())
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .totalElements(null) // Slice는 총 개수를 모름
                .build();
    }

    default <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .number(page.getNumber())
                .size(page.getSize())
                .hasNext(page.hasNext())
                .totalElements(page.getTotalElements())
                .build();
    }
}
