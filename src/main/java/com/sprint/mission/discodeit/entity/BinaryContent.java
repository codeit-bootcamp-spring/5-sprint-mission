package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class BinaryContent implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final Instant createdAt;
	private final String fileName;
	private final String contentType;
	private final Long size;
	private final byte[] bytes;

	public BinaryContent(String fileName, String contentType, Long size, byte[] bytes) {
		this.id = UUID.randomUUID();
		this.createdAt = Instant.now();
		this.fileName = fileName;
		this.contentType = contentType;
		this.size = size;
		this.bytes = bytes;
	}

	private BinaryContent(BinaryContent original) {
		this.id = original.id;
		this.createdAt = original.createdAt;
		this.fileName = original.fileName;
		this.contentType = original.contentType;
		this.size = original.size;
		this.bytes = original.bytes;
	}

	public BinaryContent copy(){
		return new BinaryContent(this);
	}
}
