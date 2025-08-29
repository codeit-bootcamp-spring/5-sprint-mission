package com.sprint.mission.discodeit.domain.entity;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BinaryContent extends BaseEntity {

	@NotNull
	private String fileName;
	@NotNull
	private String contentType;
	@NotNull
	private long size;
	@NotNull
	private byte[] bytes;

	public BinaryContent(byte[] bytes, long size, String contentType, String fileName) {
		super(UUID.randomUUID(), Instant.now());
		this.bytes = bytes;
		this.size = size;
		this.contentType = contentType;
		this.fileName = fileName;
	}

}
