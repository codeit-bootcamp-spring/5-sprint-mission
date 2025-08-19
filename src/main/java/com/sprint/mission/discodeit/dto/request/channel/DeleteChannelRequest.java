package com.sprint.mission.discodeit.dto.request.channel;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class DeleteChannelRequest {
	@NotBlank(message = "채널 ID는 필수")
	private UUID channelId;

	@NotBlank(message = "요청자 ID는 필수")
	private UUID userId;
}
