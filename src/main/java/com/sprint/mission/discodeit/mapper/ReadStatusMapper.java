// src/main/java/com/sprint/mission/discodeit/mapper/ReadStatusMapper.java

package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

  // DTO → Entity (생성용)
  public ReadStatus toEntity(ReadStatusDto dto, User user, Channel channel) {
    return new ReadStatus(user, channel, dto.getLastReadAt());
  }

  // Entity → DTO (조회용)
  public ReadStatusDto toDto(ReadStatus entity) {
    ReadStatusDto dto = new ReadStatusDto();
    dto.setId(entity.getId());
    dto.setUserId(entity.getUser().getId());
    dto.setChannelId(entity.getChannel().getId());
    dto.setLastReadAt(entity.getLastReadAt());
    return dto;
  }

  // Entity List → DTO List (모두 조회용)
  public List<ReadStatusDto> toDtoList(List<ReadStatus> entities) {
    return entities.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  // DTO → Entity 값 업데이트 (수정용)
  public void updateEntityFromDto(ReadStatus entity, ReadStatusDto dto) {
    if (dto.getNewLastReadAt() != null) {
      entity.update(dto.getNewLastReadAt());
    }
  }
}