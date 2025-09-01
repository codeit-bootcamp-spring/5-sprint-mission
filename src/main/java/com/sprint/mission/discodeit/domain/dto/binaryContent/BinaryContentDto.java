package com.sprint.mission.discodeit.domain.dto.binaryContent;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class BinaryContentDto {
	private UUID id;
	private String fileName;
	private Long size;
	private String contentType;
	byte[] bytes;
}
