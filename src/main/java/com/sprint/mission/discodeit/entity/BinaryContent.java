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
	private final String filename;
	private final String contentType;
	private final Long size;
	private final byte[] content;

	public BinaryContent(String filename, String contentType, Long size, byte[] content) {
		this.id = UUID.randomUUID();
		this.createdAt = Instant.now();
		this.filename = filename;
		this.contentType = contentType;
		this.size = size;
		this.content = content;
	}

	private BinaryContent(BinaryContent original) {
		this.id = original.id;
		this.createdAt = original.createdAt;
		this.filename = original.filename;
		this.contentType = original.contentType;
		this.size = original.size;
		this.content = original.content;
	}

	public BinaryContent copy(){
		return new BinaryContent(this);
	}
}
