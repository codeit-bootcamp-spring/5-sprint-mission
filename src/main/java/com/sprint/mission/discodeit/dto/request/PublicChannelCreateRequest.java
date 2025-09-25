package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
	@NotBlank(message = "채널 이름은 필수입니다")
	@Size(max = 100, message = "채널 이름은 100자를 초과할 수 없습니다")
	String name,

	@Size(max = 500, message = "채널 설명은 500자를 초과할 수 없습니다")
	String description
) {
}
