package com.sprint.mission.discodeit.dto.request.channel;
//UUID channelUUID, UUID userUUID, String newNickname

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UpdateUserNicknameRequest {
	@NotNull(message = "채널 ID는 필수")
	private UUID channelId;
	@NotNull(message = "사용자 ID는 필수")
	private UUID userId;
	@NotBlank(message = "새 닉네임은 필수")
	private String newNickname;
}
