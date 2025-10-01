package com.sprint.mission.discodeit.dto.response;

import java.util.List;

// 제네릭 타입 T를 받아 페이지 응답 구조를 표현하는 record 클래스 정의
public record PageResponse<T>(

        // 현재 페이지에 포함된 데이터 목록
        List<T> content,

        // 다음 페이지 요청 시 사용할 커서 값 (커서 기반 페이지네이션용)
        Object nextCursor,

        // 현재 페이지의 데이터 개수
        int size,

        // 다음 페이지가 존재하는지 여부 (true = 다음 페이지 있음)
        boolean hasNext,

        // 전체 데이터 개수 (Page 방식에서는 값 존재, Slice 방식에서는 null 가능)
        Long totalElements
) {}

