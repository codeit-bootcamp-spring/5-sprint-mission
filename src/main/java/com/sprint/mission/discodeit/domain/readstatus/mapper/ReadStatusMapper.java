package com.sprint.mission.discodeit.domain.readstatus.mapper;

import com.sprint.mission.discodeit.domain.readstatus.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "channelId", source = "channel.id")
  ReadStatusDto toDto(ReadStatus readStatus);
}
