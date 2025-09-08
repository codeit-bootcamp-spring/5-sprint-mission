package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;


@Component
public class BinaryContentMapper {
    public BinaryContentDto toDto(BinaryContent e) { // 엔티티→DTO
        if (e == null) return null;                  // 널 가드
        return new BinaryContentDto(                 // record 생성
                e.getId(),                               // id 매핑
                e.getFileName(),                         // 파일명 매핑
                e.getSize(),                             // 크기 매핑
                e.getContentType(),                      // MIME 매핑
                null                             // 바이트 매핑(정책에 맞게 조정 가능)
        );                                           // 생성 결과 반환
    }
}
