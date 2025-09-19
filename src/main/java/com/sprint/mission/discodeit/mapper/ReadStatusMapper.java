package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

public interface ReadStatusMapper {
  ReadStatusDto toDto(ReadStatus readStatus);

}
