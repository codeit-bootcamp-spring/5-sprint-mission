package com.sprint.mission.discodeit.dto.response.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;
    private Object nextCursor;
    private int size;
    private boolean hasNext;
    private Long totalElements;
}
