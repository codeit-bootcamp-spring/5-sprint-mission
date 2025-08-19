package com.sprint.mission.discodeit.dto.request.message;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetMessagesByChannelIdRequest {
	@NotBlank(message = "채널 ID는 필수")
	private UUID channelId;
}