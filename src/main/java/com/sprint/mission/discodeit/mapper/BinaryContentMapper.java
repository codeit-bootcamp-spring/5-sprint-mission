package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class BinaryContentMapper {

  // 단건: null-safe
  public BinaryContentDto toDto(BinaryContent e) {
    if (e == null) return null;
    return new BinaryContentDto(
        e.getId(),
        e.getFileName(),
        e.getSize(),
        e.getContentType()
    );
  }

  // 컬렉션: null/빈 컬렉션 안전
  public List<BinaryContentDto> toDtoList(Collection<BinaryContent> list) {
    if (list == null || list.isEmpty()) return List.of();
    return list.stream()
        .map(this::toDto)
        .filter(Objects::nonNull)
        .toList();
  }
}
