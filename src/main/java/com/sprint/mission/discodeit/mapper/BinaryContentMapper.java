package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

// MapStruct 라이브러리를 사용하여 엔티티 ↔ DTO 변환을 자동으로 처리하는 매퍼 인터페이스 정의
@Mapper(componentModel = "spring") // 스프링 빈으로 등록되도록 설정
public interface BinaryContentMapper {

    // BinaryContent 엔티티를 BinaryContentDto로 변환하는 매핑 메서드
    BinaryContentDto toDto(BinaryContent binaryContent);
}

