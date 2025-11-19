package com.sprint.mission.discodeit.dto.channel;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;

public record PrivateChannelCreateRequest(
	@NotEmpty(message = "개인 채널은 1명 이상의 사용자가 필요합니다")
	List<UUID> participantIds
) {

}
