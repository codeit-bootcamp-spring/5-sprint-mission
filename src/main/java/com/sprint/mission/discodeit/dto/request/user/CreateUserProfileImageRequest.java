package com.sprint.mission.discodeit.dto.request.user;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.BinaryContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CreateUserProfileImageRequest {
	private final String filename;
	private final String contentType;
	private final Long size;
	private final byte[] content;

	public BinaryContent toBinaryContent() {
		return BinaryContent.builder()
				.filename(filename)
				.contentType(contentType)
				.size(size)
				.content(content)
				.id(UUID.randomUUID())
				.createdAt(Instant.now())
				.build();
	}
}
