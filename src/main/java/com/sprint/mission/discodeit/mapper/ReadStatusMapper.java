package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.readStatus.ReadStatusDto;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReadStatusMapper {

	ReadStatusDto toDto(ReadStatus readStatus) {
		return ReadStatusDto.builder()
		  .id(readStatus.getId())
		  .userId(readStatus.getUser().getId())
		  .channelId(readStatus.getChannel().getId())
		  .lastReadAt(readStatus.getLastReadAt())
		  .build();

	}
}
