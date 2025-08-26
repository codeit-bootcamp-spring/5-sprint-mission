package com.sprint.mission.discodeit.dto.request.channel;

//User user, String channelName

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class JoinChannelRequest {
	@NotNull(message = "유저 ID는 필수")
	private UUID userId;
	@NotBlank(message = "유저 닉네임은 필수")
	private String userDefaultNickname;
	@NotBlank(message = "입장 채널 명은 필수")
	private String channelName;
}
