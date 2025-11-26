package com.sprint.mission.discodeit.domain.dto.binaryContent;

import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;

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

	public static BinaryContentDto of(BinaryContent binaryContent) {
		return new BinaryContentDto(
		  binaryContent.getId(),
		  binaryContent.getFileName(),
		  binaryContent.getSize(),
		  binaryContent.getContentType()
		);
	}
}
