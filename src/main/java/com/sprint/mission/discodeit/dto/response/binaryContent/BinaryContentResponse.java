package com.sprint.mission.discodeit.dto.response.binaryContent;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.BinaryContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BinaryContentResponse {
	private UUID id;
	private Instant createdAt;
	private String fileName;
	private String contentType;
	private Long size;
	private byte[] content;
	private boolean success;

	private BinaryContentResponse(BinaryContent binaryContent) {
		this.id = binaryContent.getId();
		this.createdAt = binaryContent.getCreatedAt();
		this.fileName = binaryContent.getFileName();
		this.contentType = binaryContent.getContentType();
		this.size = binaryContent.getSize();
		this.success = true;
	}

	public static BinaryContentResponse success(BinaryContent binaryContent) {
		return new BinaryContentResponse(binaryContent);
	}
}
