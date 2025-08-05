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
public class CreateUserResponse {
	private UUID id;
	private Instant createdAt;
	private String nickname;
	private String email;
	@Nullable
	private UUID profileId;

	private CreateUserResponse(User user) {
		this.id = user.getId();
		this.createdAt = user.getCreatedAt();
		this.nickname = user.getDefaultNickname();
		this.email = user.getEmail();
		this.profileId = user.getProfileId();
	}

	public static CreateUserResponse success(User user){
		return new CreateUserResponse(user);
	}
}