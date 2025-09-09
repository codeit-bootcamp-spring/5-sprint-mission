package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sprint.mission.discodeit.domain.dto.readStatus.ReadStatusDto;
import com.sprint.mission.discodeit.domain.dto.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "channel.id", target = "channelId")
	ReadStatusDto toDto(ReadStatus readStatus);

	ReadStatusResponse toResponse(ReadStatusDto dto);

}
