package com.sprint.mission.discodeit.dto.request.channel;

//UUID channelUUID, UUID userUUID

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChannelLeaveRequest {
	@NotNull(message = "채널 ID는 필수")
	private UUID channelId;
	@NotNull(message = "사용자 ID는 필수")
	private UUID userId;
}
