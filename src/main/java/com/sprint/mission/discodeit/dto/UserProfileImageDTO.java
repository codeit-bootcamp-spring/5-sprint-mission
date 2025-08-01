package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.BinaryContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserProfileImageDTO {
	private final String filename;
	private final String type;
	private final byte[] content;

	public BinaryContent toBinaryContent() {
		return BinaryContent.builder()
				.filename(filename)
				.type(type)
				.content(content)
				.id(UUID.randomUUID())
				.createdAt(Instant.now())
				.build();
	}
}
