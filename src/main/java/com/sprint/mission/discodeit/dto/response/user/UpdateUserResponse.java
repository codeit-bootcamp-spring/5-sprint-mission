package com.sprint.mission.discodeit.dto.response.user;

import java.time.Instant;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
public class UpdateUserResponse {
	private UUID id;
	private Instant createdAt;
	private Instant updatedAt;
	private String email;
	private String defaultNickname;
	@Nullable
	private UUID profileId;
	private boolean success;

	private UpdateUserResponse(User user){
		this.id = user.getId();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
		this.email = user.getEmail();
		this.defaultNickname = user.getDefaultNickname();
		this.profileId = user.getProfileId();
		this.success = true;
	}

	public static UpdateUserResponse success(User user) {
		return new UpdateUserResponse(user);
	}
}
