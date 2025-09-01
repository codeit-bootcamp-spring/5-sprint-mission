package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.domain.entity.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class BinaryContent extends BaseEntity {

	@NotNull
	private String fileName;
	@NotNull
	private String contentType;
	@NotNull
	private long size;
	// @NotNull
	// private byte[] bytes;

	public BinaryContent(long size, String contentType, String fileName) {
		// this.bytes = bytes;
		this.size = size;
		this.contentType = contentType;
		this.fileName = fileName;
	}

}
