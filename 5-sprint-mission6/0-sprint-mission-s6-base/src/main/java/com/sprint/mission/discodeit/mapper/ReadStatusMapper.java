package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

  public ReadStatusDto toDto(ReadStatus rs) {
    if (rs == null) return null;
    return new ReadStatusDto(
        rs.getId(),
        rs.getUser().getId(),
        rs.getChannel().getId(),
        rs.getLastReadAt()
    );
  }
}
