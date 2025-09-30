package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ChannelMapper {

  // Create용: DTO → Entity
  public Channel toEntity(ChannelDto dto) {
    return new Channel(dto.getName(), dto.getDescription(), dto.getChannelType());
  }

  // Entity → DTO
  public ChannelDto toDto(Channel entity) {
    ChannelDto dto = new ChannelDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setDescription(entity.getDescription());
    dto.setChannelType(entity.getChannelType());
    return dto;
  }

  // Entity List → DTO List
  public List<ChannelDto> toDtoList(List<Channel> entities) {
    return entities.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  // DTO → Entity 값 update
  public void updateEntityFromDto(Channel channel, ChannelDto dto) {
    if (dto.getNewName() != null && !dto.getNewName().isEmpty()) {
      channel.updateName(dto.getNewName());
    }
    if (dto.getNewDescription() != null && !dto.getNewDescription().isEmpty()) {
      channel.updateDescription(dto.getNewDescription());
    }
  }
}