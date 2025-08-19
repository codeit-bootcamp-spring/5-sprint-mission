package com.sprint.mission.discodeit.dto.response.binaryContent;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.BinaryContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Base64BinaryContentResponse {
	private UUID id;
	private Instant createdAt;
	private String fileName;
	private String contentType;
	private Long size;
	private String bytes;
	private boolean success;

	private Base64BinaryContentResponse(BinaryContent binaryContent) {
		this.id = binaryContent.getId();
		this.createdAt = binaryContent.getCreatedAt();
		this.fileName = binaryContent.getFileName();
		this.contentType = binaryContent.getContentType();
		this.size = binaryContent.getSize();
		this.bytes = Base64.getEncoder().encodeToString(binaryContent.getBytes());
		this.success = true;
	}

	public static Base64BinaryContentResponse success(BinaryContent binaryContent) {
		return new Base64BinaryContentResponse(binaryContent);
	}

	public static Base64BinaryContentResponse fromResponse(BinaryContentResponse response) {
		return Base64BinaryContentResponse.builder()
			.id(response.getId())
			.createdAt(response.getCreatedAt())
			.fileName(response.getFilename())
			.contentType(response.getContentType())
			.size(response.getSize())
			.bytes(Base64.getEncoder().encodeToString(response.getContent()))
			.success(response.isSuccess())
			.build();
	}
}