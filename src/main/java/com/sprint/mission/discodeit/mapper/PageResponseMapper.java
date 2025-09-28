package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

// MapStruct 매퍼: Page 또는 Slice 결과를 PageResponse DTO로 변환하는 역할
@Mapper(componentModel = "spring") // 스프링 빈으로 등록
public interface PageResponseMapper {

    // Slice<T> → PageResponse<T> 변환
    // Slice는 전체 개수를 제공하지 않으므로 totalElements 값은 null로 설정
    default <T> PageResponse<T> fromSlice(Slice<T> slice, Object nextCursor) {
        return new PageResponse<>(
                slice.getContent(),      // 현재 Slice의 데이터 목록
                nextCursor,              // 다음 페이지 요청 시 사용할 커서 값
                slice.getSize(),         // 요청된 페이지 크기
                slice.hasNext(),         // 다음 Slice 존재 여부
                null                     // Slice에는 전체 개수 정보가 없으므로 null
        );
    }

    // Page<T> → PageResponse<T> 변환
    // Page는 totalElements(전체 데이터 개수)를 제공함
    default <T> PageResponse<T> fromPage(Page<T> page, Object nextCursor) {
        return new PageResponse<>(
                page.getContent(),       // 현재 Page의 데이터 목록
                nextCursor,              // 다음 페이지 요청 시 사용할 커서 값
                page.getSize(),          // 요청된 페이지 크기
                page.hasNext(),          // 다음 Page 존재 여부
                page.getTotalElements()  // 전체 데이터 개수
        );
    }
}

