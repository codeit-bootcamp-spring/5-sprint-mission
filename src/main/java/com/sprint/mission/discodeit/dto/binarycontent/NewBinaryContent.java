package com.sprint.mission.discodeit.dto.binarycontent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewBinaryContent(
	@NotBlank String fileName,
	@NotNull String contentType,
	@NotNull byte[] bytes
) {

	public String forLog() {
		return "NewBinaryContent{" + fileName + ", " + contentType + "}";
	}

}
