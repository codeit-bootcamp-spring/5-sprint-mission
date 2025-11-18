package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(

	@Size(max = 100, message = "채널명은 최대 100자 까지 가능합니다")
	String newName,

	@Size(max = 500, message = "채널 설명은 최대 500자 까지 가능합니다")
	String newDescription
) {

}
