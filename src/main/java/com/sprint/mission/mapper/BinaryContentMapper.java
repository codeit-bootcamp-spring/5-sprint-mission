package com.sprint.mission.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;

@Component
public class BinaryContentMapper {

	public BinaryContentDto toDto(BinaryContent content) {
		return BinaryContentDto.builder()
		  .id(content.getId())
		  .fileName(content.getFileName())
		  .size(content.getSize())
		  .contentType(content.getContentType())
		  .bytes(content.getBytes())
		  .build();
	}
}
