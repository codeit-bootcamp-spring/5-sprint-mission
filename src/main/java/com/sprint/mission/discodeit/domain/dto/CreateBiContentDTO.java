package com.sprint.mission.discodeit.domain.dto;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class CreateBiContentDTO {
	@NotNull
	@Size(min = 1, message = "Content size must be at least 1 byte")
	private final byte[] content;
	@NotNull
	@Min(value = 1, message = "size must be at least 1 byte")
	private final long size;
	@NotNull
	private final String contentType;
	@NotNull
	@NotBlank
	private final String fileName;

	public BinaryContent toBinaryContent() {
		return new BinaryContent(size, contentType, fileName);
	}

}
