package com.sprint.mission.discodeit.dto.response.user;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetUserResponse {
	private UUID id;
	private String nickname;
	private String email;
	private Instant createdAt;
	private Instant updatedAt;
	@Nullable
	private UUID profileId;

	private GetUserResponse(User user) {
		this.id = user.getId();
		this.nickname = user.getDefaultNickname();
		this.email = user.getEmail();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
		this.profileId = user.getProfileId();
	}

	public static GetUserResponse success(User user) {
		return new GetUserResponse(user);
	}
}
