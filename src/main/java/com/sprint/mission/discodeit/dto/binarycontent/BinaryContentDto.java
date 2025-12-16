package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;

import lombok.Builder;

@Builder
public record BinaryContentDto(
	UUID id,
	String fileName,
	Long size,
	String contentType,
	BinaryContentStatus status
) {

}
