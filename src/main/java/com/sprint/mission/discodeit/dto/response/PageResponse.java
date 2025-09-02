package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PageResponse <T>{
    public List<T> content;
    public int number;
    public int size;
    public boolean hasNext;
    public Long totalElements;
}
