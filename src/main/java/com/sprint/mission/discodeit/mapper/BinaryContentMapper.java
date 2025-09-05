package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
  public BinaryContentDto toDto(BinaryContent e) {
    return new BinaryContentDto(e.getId(), e.getFileName(), e.getSize(), e.getContentType());
  }
}
