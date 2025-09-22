package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PrivateChannelCreateRequest(
	@NotEmpty(message = "참가자 목록은 필수입니다.")
	@Size(min = 2, message = "비공개 채널은 최소 2명의 참가자가 필요합니다.")
    List<UUID> participantIds
) {

}
