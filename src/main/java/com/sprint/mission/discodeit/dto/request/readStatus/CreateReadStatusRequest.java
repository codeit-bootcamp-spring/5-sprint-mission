package com.sprint.mission.discodeit.dto.request.readStatus;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CreateReadStatusRequest {
	@NotBlank(message = "사용자 ID는 필수")
	private UUID userId;
	@NotBlank(message = "채널 ID는 필수")
	private UUID channelId;
}
