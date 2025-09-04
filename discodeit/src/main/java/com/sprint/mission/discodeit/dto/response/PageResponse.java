package com.sprint.mission.discodeit.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;


@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    public List<T> content;  //실제 데이터
    public Object nextCursor;
    public int size; //페이지 크기
    public boolean hasNext; //다음 존재 여부
    public Long totalElements; //T데이터의 총 갯수를 의미하며, null도 가능
}

