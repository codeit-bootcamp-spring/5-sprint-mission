package com.sprint.mission.discodeit.dto.request.channel;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UpdateChannelnameRequest {
	@NotNull(message = "채널 ID는 필수")
	private UUID channelId;
	@NotBlank(message = "채널 명은 필수")
	private String channelNewName;
}
