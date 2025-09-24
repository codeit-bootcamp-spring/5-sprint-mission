package com.sprint.mission.discodeit.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UpdateMessageRequest {

	@NotBlank(message = "newContent 는 비어있을 수 없습니다.")
	private final String newContent;
}
