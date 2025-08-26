package com.sprint.mission.discodeit.dto.request.channel;

//UUID channelUUID, UUID userUUID

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class LeaveChannelRequest {
	@NotNull(message = "채널 ID는 필수")
	private UUID channelId;
	@NotNull(message = "사용자 ID는 필수")
	private UUID userId;
	@NotBlank(message = "유저 닉네임은 필수")
	private String userDafultNickname;
}
