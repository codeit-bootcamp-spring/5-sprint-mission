package com.sprint.mission.discodeit.dto.response.auth;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponse {
	private UUID id;
	private Instant createdAt;
	private Instant updatedAt;
	private String email;
	private String username;
	private String defaultNickname;
	@Nullable
	private UUID profileId;
	private boolean success;

	private LoginResponse(User user) {
		this.id = user.getId();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
		this.email = user.getEmail();
		this.username = user.getUsername();
		this.defaultNickname = user.getDefaultNickname();
		this.profileId = user.getProfileId();
		this.success = true;
	}

	public static LoginResponse success(User user) {
		return new LoginResponse(user);
	}
}
