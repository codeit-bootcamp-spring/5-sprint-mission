package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
  public BinaryContentDto toDto(BinaryContent entity) {
    if (entity == null) return null;
    return new BinaryContentDto(
        entity.getId(),
        entity.getFileName(),
        entity.getSize(),
        entity.getContentType(),
        entity.getBytes() // ※ 스토리지 분리 전 단계 — 이후 제거 예정
    );
  }
}
