package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PageResponseMapper {

    // T가 이미 DTO인 경우
    public <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return new PageResponse<>(
                slice.getContent(), slice.getNumber(), slice.getSize(), slice.hasNext(), null
        );
        // totalElements는 모름 → null
    }

    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(), page.getNumber(), page.getSize(), page.hasNext(), page.getTotalElements()
        );
    }

    // E(Entity) → D(DTO) 변환 포함 버전 (컨트롤러에서 편하게 사용)
    public <E, D> PageResponse<D> fromSlice(Slice<E> slice, Function<E, D> mapper) {
        return new PageResponse<>(
                slice.getContent().stream().map(mapper).toList(),
                slice.getNumber(), slice.getSize(), slice.hasNext(), null
        );
    }

    public <E, D> PageResponse<D> fromPage(Page<E> page, Function<E, D> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(), page.getSize(), page.hasNext(), page.getTotalElements()
        );
    }
}
