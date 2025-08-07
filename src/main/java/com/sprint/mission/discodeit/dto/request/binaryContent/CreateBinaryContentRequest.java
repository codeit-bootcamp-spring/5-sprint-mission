package com.sprint.mission.discodeit.dto.request.binaryContent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CreateBinaryContentRequest {
	@NotBlank(message = "파일명은 필수")
	private String filename;

	@NotBlank(message = "컨텐츠 타입은 필수")
	private String contentType;

	@NotNull(message = "파일 크기는 필수")
	@Positive(message = "파일 크기는 0보다 커야 합니다")
	private Long size;

	@NotNull(message = "파일 내용은 필수")
	private byte[] content;
}