package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

  //DTO -> Entity (생성용)
  public BinaryContent toEntity(BinaryContentDto dto) {
    return new BinaryContent(
        dto.getFileName(),
        dto.getContentType(),
        dto.getSize(),
        dto.getData()
    );
  }

  //Entity -> DTO (조회용)
  public BinaryContentDto toDto(BinaryContent entity) {
    BinaryContentDto dto = new BinaryContentDto();
    dto.setId(entity.getId());
    dto.setFileName(entity.getFileName());
    dto.setContentType(entity.getContentType());
    dto.setData(entity.getBytes());
    return dto;
  }

  //Entity List -> DTO List( 여러 파일 조회용)
  public List<BinaryContentDto> toDtoList(List<BinaryContent> entities) {
    return entities.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }
}
